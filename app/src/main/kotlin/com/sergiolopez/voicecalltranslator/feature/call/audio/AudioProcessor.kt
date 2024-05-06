package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.content.Context
import android.os.Build
import android.os.Environment
import com.sergiolopez.voicecalltranslator.feature.call.audio.WavFileBuilder.createWavFileFromByteBufferList
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
    private val context: Context
) : AudioRecordDataCallback {

    private lateinit var scope: CoroutineScope

    private val bufferMiddleQueue: Queue<ByteBuffer> = LinkedList()
    //private val bufferMiddleQueue: Queue<ByteBuffer> = ConcurrentLinkedQueue()

    private val byteBufferList = mutableListOf<ByteBuffer>()

    private val delayBuffersCount = 300  // 3 seconds delay with expected 10ms buffer after reading

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
            createWavFileFromByteBufferList(
                byteBufferList = byteBufferList,
                output = outputFile
            )
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