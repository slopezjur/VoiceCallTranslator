package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.content.Context
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioFileManager.buildNameFile
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioFileManager.createOutputFile
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioFileManager.deleteFile
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioProcessorBuilder.createWavFileFromByteArray
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioProcessorBuilder.createWavFileFromByteBufferList
import com.sergiolopez.voicecalltranslator.feature.call.data.repository.VctMagicRepository
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

class AudioProcessor @Inject constructor(
    private val context: Context,
    private val vctMagicRepository: VctMagicRepository
) : AudioRecordDataCallback {

    private lateinit var scope: CoroutineScope

    //private val bufferMiddleQueue: Queue<ByteBuffer> = LinkedList()
    private val bufferMiddleQueue: Queue<ByteBuffer> = ConcurrentLinkedQueue()

    private val byteBufferList = mutableListOf<ByteBuffer>()

    private val minimumDelayBuffer = 300  // Every "100" delay represents 10ms buffer from WebRTC

    private var fileIdentifier = 0

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }

    override fun onAudioDataRecorded(
        audioFormat: Int,
        channelCount: Int,
        sampleRate: Int,
        audioBuffer: ByteBuffer
    ) {
        // Clone buffer
        val length = audioBuffer.limit() - audioBuffer.position()

        val audioBufferCopy = ByteBuffer.allocate(length)
        audioBufferCopy.put(audioBuffer.array(), audioBuffer.position(), length)
        audioBufferCopy.flip()  // Prepare next reading

        val audioBufferCopyAux = ByteBuffer.allocate(length)
        audioBufferCopyAux.put(audioBufferCopy.array(), audioBufferCopy.position(), length)
        audioBufferCopyAux.flip()  // Prepare next reading

        // Add cloned buffer to the queue
        //bufferMiddleQueue.add(audioBufferCopy)
        byteBufferList.add(audioBufferCopyAux)

        // Clean remote buffer to create latency
        audioBuffer.clear()

        if (byteBufferList.size >= minimumDelayBuffer - 1) {
            Log.d(VCT_MAGIC, "bufferMiddleQueue size ${bufferMiddleQueue.size}")
            processByteBufferList(byteBufferList = byteBufferList.toList())
            Log.d(VCT_MAGIC, "byteBufferList full")
            byteBufferList.clear()
            Log.d(VCT_MAGIC, "byteBufferList clear")
        }

        if (bufferMiddleQueue.size >= minimumDelayBuffer) {
            val delayedAudioBuffer = bufferMiddleQueue.poll()  // Get oldest buffer
            delayedAudioBuffer?.let {
                audioBuffer.put(it)
            } ?: {
                sendSilence(
                    audioBufferCopy = audioBufferCopy,
                    audioBuffer = audioBuffer
                )
                Log.d(VCT_MAGIC, "bufferMiddleQueue empty -> audioBuffer silence")
            }
        } else {
            sendSilence(
                audioBufferCopy = audioBufferCopy,
                audioBuffer = audioBuffer
            )
        }
        audioBuffer.flip()  // Prepare next round
    }

    private fun sendSilence(audioBufferCopy: ByteBuffer, audioBuffer: ByteBuffer) {
        if (audioBufferCopy.remaining() >= audioBuffer.remaining()) {
            val silence = ByteArray(audioBuffer.capacity())
            audioBuffer.put(silence)
        } else {
            Log.d(
                VCT_MAGIC,
                "audioBufferCopy.remaining() ${audioBufferCopy.remaining()} " +
                        "audioBuffer.remaining() ${audioBuffer.remaining()}"
            )
        }
    }

    private fun processByteBufferList(byteBufferList: List<ByteBuffer>) {
        val fileName = buildNameFile(
            fileIdentifier = fileIdentifier
        )
        scope.launch(Dispatchers.IO) {
            Log.d(VCT_MAGIC, "createWavFile Start")
            val outputFile = createOutputFile(
                context = context,
                nameFile = fileName
            )

            val wavFileCreated = createWavFileFromByteBufferList(
                byteBufferList = byteBufferList,
                output = outputFile
            )

            Log.d(VCT_MAGIC, "createWavFile End: $wavFileCreated")
            if (wavFileCreated) {

                val audioTranscription = getAudioTranscription(
                    outputFile = outputFile
                )

                val translatedText = getTextTranslation(
                    audioTranscription = audioTranscription
                )

                val byteArraySpeech = translatedText?.let {
                    if (it.isNotBlank()) {
                        getAudioSpeech(
                            translatedText = it
                        )
                    } else null
                }

                byteArraySpeech?.let {
                    AudioProcessorBuilder.fillBufferQueueFromWavByteArray(
                        rawAudioBytes = it,
                        bufferMiddleQueue = bufferMiddleQueue
                    )
                    // Testing purpose
                    createWavFileFromByteArray(
                        it, createOutputFile(
                            context = context,
                            nameFile = buildNameFile(
                                fileIdentifier = fileIdentifier
                            )
                        )
                    )
                }
                fileIdentifier++
                deleteFile(filePath = outputFile.path)
            }
        }
    }

    private suspend fun getAudioTranscription(outputFile: File): String {
        val audioTranscription = vctMagicRepository.speechToText(
            outputFile = outputFile
        )
        Log.d(VCT_MAGIC, "audioTranscription: $audioTranscription")

        return audioTranscription
    }

    private suspend fun getTextTranslation(audioTranscription: String): String? {
        val translatedText = vctMagicRepository.translation(
            textToTranslate = audioTranscription
        )
        Log.d(VCT_MAGIC, "translatedText: $translatedText")

        return translatedText
    }

    private suspend fun getAudioSpeech(translatedText: String): ByteArray {
        val audioRaw = vctMagicRepository.textToSpeech(
            textToSpeech = translatedText
        )
        Log.d(VCT_MAGIC, "textToSpeech: ${audioRaw.size}")

        return audioRaw
    }
}