package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.audio.AudioProcessorBuilder.createWavFileFromByteArray
import com.sergiolopez.voicecalltranslator.feature.call.magiccreator.VctMagicCreator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.audio.AudioRecordDataCallback
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.LinkedList
import java.util.Locale
import java.util.Queue
import javax.inject.Inject

class AudioProcessor @Inject constructor(
    private val context: Context,
    private val vctMagicCreator: VctMagicCreator
) : AudioRecordDataCallback {

    private lateinit var scope: CoroutineScope

    private val bufferMiddleQueue: Queue<ByteBuffer> = LinkedList()
    //private val bufferMiddleQueue: Queue<ByteBuffer> = ConcurrentLinkedQueue()

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
        bufferMiddleQueue.add(audioBufferCopy)
        byteBufferList.add(audioBufferCopyAux)

        // Clean remote buffer to create latency
        audioBuffer.clear()

        if (bufferMiddleQueue.size >= delayBuffersCount) {
            if (byteBufferList.size >= delayBuffersCount - 1) {
                createWavFile(
                    byteBufferList = byteBufferList.toList()
                )
                byteBufferList.clear()
            }

            val delayedAudioBuffer = bufferMiddleQueue.poll()  // Get oldest buffer
            delayedAudioBuffer?.let {
                audioBuffer.put(it)
            }
        } else {
            if (audioBufferCopy.remaining() >= audioBuffer.remaining()) {
                val silence = ByteArray(audioBuffer.capacity())
                audioBuffer.put(silence)
            }
        }
        audioBuffer.flip()  // Prepare next round
    }

    private fun createWavFile(byteBufferList: List<ByteBuffer>) {
        val outputFile = createOutputFile()
        scope.launch(Dispatchers.IO) {
            Log.d("VCT_MAGIC", "createWavFile Start")
            /*val wavFileCreated = createWavFileFromByteBufferList(
                byteBufferList = byteBufferList,
                output = outputFile
            )
            Log.d("VCT_MAGIC", "createWavFile End: $wavFileCreated")
            if (wavFileCreated) {
                //val audioTranscription = "Esto es una prueba de audio, hermano!"
                //val textTranslation = "This is an audio test, brother!"

                /*val audioTranscription = getAudioTranscription(
                    outputFile = outputFile
                )
                val translatedText = getTextTranslation(
                    audioTranscription = audioTranscription
                )*/
            }*/
            val result =
                vctMagicCreator.textToSpeech("Esta es una oveja negra que pastaba por el campo junto a sus amigos los lobos. Cuando llegaba la noche se iban a dormir.")
            AudioProcessorBuilder.fillBufferFromPcmByteArray(result, bufferMiddleQueue)
            Log.d("VCT_MAGIC", "createWavFileFromByteArray Start")
            val wavFileCreated = createWavFileFromByteArray(result, outputFile)
            Log.d("VCT_MAGIC", "createWavFileFromByteArray End")
        }
    }

    private suspend fun getAudioTranscription(outputFile: File): String {
        val audioTranscription = vctMagicCreator.speechToText(
            outputFile = outputFile
        )
        Log.d("VCT_MAGIC", "audioTranscription: $audioTranscription")

        return audioTranscription
    }

    private suspend fun getTextTranslation(audioTranscription: String): String? {
        val translatedText = vctMagicCreator.translation(
            textToTranslate = audioTranscription
        )
        Log.d("VCT_MAGIC", "translatedText: $translatedText")

        return translatedText
    }

    /*private suspend fun getAudioSpeech(translatedText: String): ByteArray {
        val audioRaw = vctMagicCreator.textToSpeech(
            textToSpeech = translatedText
        )
        Log.d("VCT_MAGIC", "textToSpeech: $audioRaw")

        return audioRaw
    }*/

    private fun createOutputFile(): File {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
        val date = dateFormat.format(Date())
        val output = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val path = context.getExternalFilesDir(Environment.DIRECTORY_RECORDINGS)
            File(
                path,
                "recording_$date.wav"
            )
        } else {
            val path = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)
            File(
                path,
                "recording_$date.wav"
            )
        }

        return output
    }
}