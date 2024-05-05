package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.call.audio.WavFileBuilder.createWavFileFromByteBufferList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.webrtc.audio.AudioRecordDataCallback
import java.io.File
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Queue
import java.util.concurrent.ConcurrentLinkedQueue
import javax.inject.Inject

class AudioProcessor @Inject constructor(
    private val context: Context
) : AudioRecordDataCallback {

    private lateinit var scope: CoroutineScope

    //private val bufferMiddleQueue: Queue<ByteBuffer> = LinkedList()
    private val bufferMiddleQueue: Queue<ByteBuffer> = ConcurrentLinkedQueue()

    private val delayBuffersCount = 300  // 3 seconds delay with expected 10ms buffer after reading
    private var count = 1

    override fun onAudioDataRecorded(
        audioFormat: Int,
        channelCount: Int,
        sampleRate: Int,
        audioBuffer: ByteBuffer
    ) {
        // Clone buffer
        val audioBufferCopy = ByteBuffer.allocate(audioBuffer.capacity())
        audioBufferCopy.put(audioBuffer.array(), audioBuffer.position(), audioBuffer.limit())
        audioBufferCopy.flip()  // Prepare next reading

        // Add cloned buffer to the queue
        bufferMiddleQueue.add(audioBufferCopy)

        // Clean remote buffer to create latency
        audioBuffer.clear()

        if (bufferMiddleQueue.size == delayBuffersCount) {
            //Log.d("AudioProcessor", "bufferQueue.size: ${bufferMiddleQueue.size}")
            /*if (count == delayBuffersCount) {
                Log.d("AudioProcessor", "createOutputFile")
                val outputFile = createOutputFile()
                createWavFile(
                    outputFile = outputFile,
                    byteBufferList = bufferMiddleQueue.toList()
                )
                count = 0
            }*/
            // If queue is ready
            val delayedBuffer = bufferMiddleQueue.poll()  // Get oldest buffer
            delayedBuffer?.let { audioBuffer.put(it) }

            //Log.d("AudioProcessor", "delayedBuffer?.let { audioBuffer.put(it) }")
        } else {
            // Send empty sound if reading is being faster
            val silence = ByteArray(audioBuffer.capacity())
            audioBuffer.put(silence)

            //Log.d("AudioProcessor", "audioBuffer.put(silence)")
        }
        audioBuffer.flip()  // Prepare next round

        count++
    }

    private fun createWavFile(outputFile: File, byteBufferList: List<ByteBuffer>) {
        scope.launch(Dispatchers.IO) {
            try {
                createWavFileFromByteBufferList(
                    buffers = byteBufferList,
                    output = outputFile
                )
                Log.d(
                    "AudioProcessor",
                    "createWavFileFromByteBufferList: ${bufferMiddleQueue.size}"
                )
            } catch (e: Exception) {
                Log.e("AudioProcessor", "Error creating WAV file", e)
            }
        }
    }

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

    fun setScope(scope: CoroutineScope) {
        this.scope = scope
    }
}