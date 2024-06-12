package com.sergiolopez.voicecalltranslator.feature.call.domain.audio

import android.content.Context
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.data.repository.MagicAudioRepository
import com.sergiolopez.voicecalltranslator.feature.call.domain.audio.AudioFileManager.buildNameFile
import com.sergiolopez.voicecalltranslator.feature.call.domain.audio.AudioFileManager.createOutputFile
import com.sergiolopez.voicecalltranslator.feature.call.domain.audio.AudioFileManager.deleteFile
import com.sergiolopez.voicecalltranslator.feature.call.domain.audio.AudioProcessorBuilder.createWavFileFromByteBufferList
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_MAGIC
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.audio.AudioRecordDataCallback
import java.io.File
import java.nio.ByteBuffer
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject
import kotlin.math.sqrt

class MagicAudioProcessor @Inject constructor(
    private val context: Context,
    private val magicAudioRepository: MagicAudioRepository
) : AudioRecordDataCallback {

    private lateinit var scope: CoroutineScope
    private var isMagicNeeded: Boolean = false
    private var audioEnabled: Boolean = true

    //private val bufferMiddleQueue: Queue<ByteBuffer> = LinkedList()
    private val bufferMiddleQueue: Queue<ByteBuffer> = ConcurrentLinkedQueue()

    private val byteBufferList = mutableListOf<ByteBuffer>()

    private val minimumDelayBuffer = 300  // 3 seconds delay to start processing audio
    private val oneSecondFromWebRtc = 100  // Each "100" represents 10ms buffer from WebRTC

    @Volatile
    private var counter: Long = 0

    private var fileIdentifier = 0

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    fun initialize(userId: String) {
        scope.launch {
            magicAudioRepository.initializeSyntheticVoice(
                userId = userId
            )
        }
        scope.launch {
            magicAudioRepository.initializeLanguageOption(
                userId = userId
            )
        }
    }

    fun setIsMagicNeeded(isMagicNeeded: Boolean, targetLanguage: String) {
        this.isMagicNeeded = isMagicNeeded
        magicAudioRepository.setTargetLanguage(
            targetLanguage = targetLanguage
        )
    }

    fun cleanResources() {
        isMagicNeeded = false
        bufferMiddleQueue.clear()
        byteBufferList.clear()
        counter = 0
        fileIdentifier = 0
    }

    fun setAudioEnabled(audioEnabled: Boolean) {
        this.audioEnabled = audioEnabled
    }

    override fun onAudioDataRecorded(
        audioFormat: Int,
        channelCount: Int,
        sampleRate: Int,
        audioBuffer: ByteBuffer
    ) {
        if (isMagicNeeded) {
            // Clone buffer
            val length = audioBuffer.limit() - audioBuffer.position()

            val audioBufferQueue = ByteBuffer.allocate(length)
            audioBufferQueue.put(audioBuffer.array(), audioBuffer.position(), length)
            audioBufferQueue.flip()  // Prepare next reading

            if (hasSound(audioBuffer = audioBuffer) && audioEnabled) {
                val audioBufferList = ByteBuffer.allocate(length)
                audioBufferList.put(audioBufferQueue.array(), audioBufferQueue.position(), length)
                audioBufferList.flip()  // Prepare next reading

                byteBufferList.add(audioBufferList)

                if (byteBufferList.size >= minimumDelayBuffer) {
                    processAudio()
                }
            } else {
                if ((counter >= minimumDelayBuffer - 1) && (byteBufferList.size >= oneSecondFromWebRtc)) {
                    processAudio()
                }
            }

            // Clean remote buffer to create silence
            audioBuffer.clear()

            val delayedAudioBuffer = bufferMiddleQueue.poll()  // Get oldest audio buffer
            delayedAudioBuffer?.let {
                audioBuffer.put(it)
            } ?: sendSilence(
                audioBufferQueue = audioBufferQueue,
                audioBuffer = audioBuffer
            )

            counter++

            audioBuffer.flip()  // Prepare next round
        }
    }

    private fun processAudio() {
        Log.d(VCT_MAGIC, "byteBufferList size ${byteBufferList.size}")
        processByteBufferList(byteBufferList = byteBufferList.toList())
        Log.d(VCT_MAGIC, "counter $counter")
        byteBufferList.clear()
        counter = 0
        Log.d(VCT_MAGIC, "byteBufferList clear")
    }

    private fun hasSound(audioBuffer: ByteBuffer): Boolean {
        val copyBuffer = audioBuffer.duplicate()
        copyBuffer.rewind()

        val samples = ShortArray(copyBuffer.remaining() / 2)

        for (i in samples.indices) {
            samples[i] = ((copyBuffer.get().toInt() and 0xFF) or (copyBuffer.get()
                .toInt() shl 8)).toShort()
        }

        var rms = 0.0
        for (sample in samples) {
            rms += (sample * sample).toDouble()
        }
        rms = sqrt(rms / samples.size)

        //Log.d(VCT_MAGIC, "rms threshold: $rms")

        return rms > 7
    }

    private fun sendSilence(audioBufferQueue: ByteBuffer, audioBuffer: ByteBuffer) {
        if (audioBufferQueue.remaining() >= audioBuffer.remaining()) {
            val silence = ByteArray(audioBuffer.capacity())
            audioBuffer.put(silence)
        } else {
            Log.d(
                VCT_MAGIC,
                "audioBufferCopy.remaining() ${audioBufferQueue.remaining()} " +
                        "audioBuffer.remaining() ${audioBuffer.remaining()}"
            )
        }
    }

    private fun processByteBufferList(byteBufferList: List<ByteBuffer>) {
        val fileName = buildNameFile(
            fileIdentifier = fileIdentifier
        )
        Log.d(VCT_MAGIC, "createWavFile Start")
        val outputFile = createOutputFile(
            context = context,
            nameFile = fileName
        )
        scope.launch(Dispatchers.IO) {
            val wavFileCreated = createWavFileFromByteBufferList(
                byteBufferList = byteBufferList,
                output = outputFile
            )

            Log.d(VCT_MAGIC, "createWavFile End: $wavFileCreated")
            if (wavFileCreated) {

                val audioTranscription = getAudioTranscription(
                    outputFile = outputFile
                )
                val containsKnownHallucination = containsKnownHallucination(
                    audioTranscription = audioTranscription
                )

                Log.d(VCT_MAGIC, "containsKnownHallucination: $containsKnownHallucination")

                if (!containsKnownHallucination && audioTranscription.isNotBlank()) {
                    updateCallTranscriptionHistory(
                        audioTranscription = audioTranscription
                    )

                    val translatedText = if (audioTranscription.isNotBlank()) {
                        getTextTranslation(
                            audioTranscription = audioTranscription
                        )
                    } else {
                        null
                    }

                    val byteArraySpeech = translatedText?.let {
                        if (it.isNotBlank()) {
                            getAudioSpeech(
                                translatedText = it
                            )
                        } else {
                            null
                        }
                    }

                    byteArraySpeech?.let {
                        AudioProcessorBuilder.fillBufferQueueFromWavByteArray(
                            rawAudioBytes = it,
                            bufferMiddleQueue = bufferMiddleQueue
                        )
                        // Testing purpose
                        /*createWavFileFromByteArray(
                            it, createOutputFile(
                                context = context,
                                nameFile = buildNameFile(
                                    fileIdentifier = fileIdentifier
                                )
                            )
                        )*/
                    }
                }

                fileIdentifier++

                // Uncomment to keep user recordings
                deleteFile(filePath = outputFile.path)
            }
        }
    }

    // https://github.com/openai/whisper/discussions/928
    private fun containsKnownHallucination(audioTranscription: String): Boolean {
        val knownHallucinationList = listOf(
            "¡Gracias por ver!",
            "¡Gracias por ver el vídeo!",
            "Subtítulos realizados por la comunidad de amara.org",
            "Subtitulado por la comunidad de Amara.org",
            "Subtítulos por la comunidad de Amara.org",
            "Subtítulos creados por la comunidad de Amara.org",
            "Subtítulos en español de Amara.org",
            "Subtítulos hechos por la comunidad de Amara.org",
            "Subtitulos por la comunidad de Amara.org",
            "Más información www.alimmenta.com",
            "www.mooji.org",
            "Para más información, visita www.alimmenta.com"
        )

        return knownHallucinationList.contains(audioTranscription)
    }

    private suspend fun getAudioTranscription(
        outputFile: File,
    ): String {
        val audioTranscription = magicAudioRepository.speechToText(
            outputFile = outputFile
        )
        Log.d(VCT_MAGIC, "audioTranscription: $audioTranscription")

        return audioTranscription
    }

    private fun updateCallTranscriptionHistory(
        audioTranscription: String
    ) {
        magicAudioRepository.updateCallTranscriptionHistory(
            audioTranscription = audioTranscription
        )
    }

    private suspend fun getTextTranslation(audioTranscription: String): String? {
        val translatedText = magicAudioRepository.translation(
            textToTranslate = audioTranscription
        )
        Log.d(VCT_MAGIC, "translatedText: $translatedText")

        return translatedText
    }

    private suspend fun getAudioSpeech(translatedText: String): ByteArray {
        val audioRaw = magicAudioRepository.textToSpeech(
            textToSpeech = translatedText
        )
        Log.d(VCT_MAGIC, "textToSpeech: ${audioRaw.size}")

        return audioRaw
    }
}