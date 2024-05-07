package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.content.Context
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioFileManager.createOutputFile
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioFileManager.deleteFile
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioProcessorBuilder.createWavFileFromByteBufferList
import com.sergiolopez.voicecalltranslator.feature.call.magiccreator.VctMagicCreator
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
    private val vctMagicCreator: VctMagicCreator
) : AudioRecordDataCallback {

    private lateinit var scope: CoroutineScope

    //private val bufferMiddleQueue: Queue<ByteBuffer> = LinkedList()
    private val bufferMiddleQueue: Queue<ByteBuffer> = ConcurrentLinkedQueue()

    private val byteBufferList = mutableListOf<ByteBuffer>()

    private val delayBuffersCount = 300  // 3 seconds delay with expected 10ms buffer after reading

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

        if (byteBufferList.size >= delayBuffersCount - 1) {
            processByteBufferList(
                byteBufferList = byteBufferList.toList()
            )
            Log.d(VCT_MAGIC, "byteBufferList full")
            byteBufferList.clear()
            Log.d(VCT_MAGIC, "byteBufferList clear")
        }

        if (bufferMiddleQueue.size >= delayBuffersCount) {
            Log.d(VCT_MAGIC, "bufferMiddleQueue full")

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
        val outputFile = createOutputFile(
            context = context
        )
        scope.launch(Dispatchers.IO) {
            Log.d(VCT_MAGIC, "createWavFile Start")
            val wavFileCreated = createWavFileFromByteBufferList(
                byteBufferList = byteBufferList,
                output = outputFile
            )
            Log.d(VCT_MAGIC, "createWavFile End: $wavFileCreated")
            if (wavFileCreated) {
                //val audioTranscription = "Esto es una prueba de audio, hermano!"
                //val textTranslation = "This is an audio test, brother!"
                val audioTranscription = getAudioTranscription(
                    outputFile = outputFile
                )

                val translatedText = getTextTranslation(
                    audioTranscription = audioTranscription
                )

                val textToSpeech = translatedText?.let {
                    getAudioSpeech(
                        translatedText = translatedText
                    )
                }

                textToSpeech?.let {
                    AudioProcessorBuilder.fillBufferFromWavByteArray(
                        rawAudioBytes = textToSpeech,
                        bufferMiddleQueue = bufferMiddleQueue
                    )
                }
                deleteFile(
                    filePath = outputFile.path
                )
            }
            //val result = vctMagicCreator.textToSpeech("Esta es una oveja negra que pastaba por el campo junto a sus amigos los lobos. Cuando llegaba la noche se iban a dormir.")
            //AudioProcessorBuilder.fillBufferFromWavByteArray(result, bufferMiddleQueue)
            //Log.d("VCT_MAGIC", "createWavFileFromByteArray Start")
            //val wavFileCreated = createWavFileFromByteArray(result, outputFile)
            //Log.d("VCT_MAGIC", "createWavFileFromByteArray End")
        }
    }

    private suspend fun getAudioTranscription(outputFile: File): String {
        val audioTranscription = vctMagicCreator.speechToText(
            outputFile = outputFile
        )
        Log.d(VCT_MAGIC, "audioTranscription: $audioTranscription")

        return audioTranscription
    }

    private suspend fun getTextTranslation(audioTranscription: String): String? {
        val translatedText = vctMagicCreator.translation(
            textToTranslate = audioTranscription
        )
        Log.d(VCT_MAGIC, "translatedText: $translatedText")

        return translatedText
    }

    private suspend fun getAudioSpeech(translatedText: String): ByteArray {
        val audioRaw = vctMagicCreator.textToSpeech(
            textToSpeech = translatedText
        )

        Log.d(VCT_MAGIC, "textToSpeech: ${audioRaw.size}")

        return audioRaw
    }
}