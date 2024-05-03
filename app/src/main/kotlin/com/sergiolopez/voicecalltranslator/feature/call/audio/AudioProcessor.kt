package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import org.webrtc.audio.AudioRecordDataCallback
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
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

    private val bufferQueue: Queue<ByteBuffer> = ConcurrentLinkedQueue() //LinkedList()
    private val delayBuffersCount = 300  // 3 seconds delay with expected 10ms buffer after reading
    private var count = 0

    private val byteBufferList = mutableListOf<ByteBuffer>()

    override fun onAudioDataRecorded(
        audioFormat: Int,
        channelCount: Int,
        sampleRate: Int,
        audioBuffer: ByteBuffer
    ) {
        // Clone buffer
        val bufferCopy = ByteBuffer.allocate(audioBuffer.capacity())
        bufferCopy.put(audioBuffer.array(), audioBuffer.position(), audioBuffer.limit())
        bufferCopy.flip()  // Prepare next reading

        // Add cloned buffer to the queue
        bufferQueue.add(bufferCopy)

        // Clean remote buffer to create latency
        audioBuffer.clear()

        /*if (count == delayBuffersCount) {
            Log.d("WebRTCClient", "delayBuffersCount: delayBuffersCount");
            scope.launch(Dispatchers.IO) {
            }
            count = 0
        }*/

        if (bufferQueue.size > delayBuffersCount) {

            // Create Wav
            /*val copyBuffer = ByteBuffer.allocate(audioBuffer.capacity())
            copyBuffer.put(audioBuffer.array(), audioBuffer.position(), audioBuffer.limit())
            copyBuffer.flip()
            byteBufferList.add(copyBuffer)

            if (byteBufferList.size == delayBuffersCount) {
                createWavFileFromByteBufferList(byteBufferList.toList())
                byteBufferList.clear()
            }*/

            // If queue is ready
            val delayedBuffer = bufferQueue.poll()  // Get oldest buffer
            delayedBuffer?.let { audioBuffer.put(it) }
        } else {
            // Send empty sound if reading is being faster
            val silence = ByteArray(audioBuffer.capacity())
            audioBuffer.put(silence)
        }
        audioBuffer.flip()  // Prepare next round

        count++
    }

    private fun concatenateBuffers(): ByteBuffer {
        val totalSize: Int
        val concatenated: ByteBuffer
        synchronized(bufferQueue) {
            totalSize = bufferQueue.sumOf { it.remaining() }
            concatenated = ByteBuffer.allocate(totalSize)
            val iter = bufferQueue.iterator()
            while (iter.hasNext()) {
                concatenated.put(iter.next())
            }
            concatenated.flip()
        }
        return concatenated
    }

    private fun createWavFileFromByteBufferList(
        buffers: List<ByteBuffer>,
        channelCount: Int = 1,
        sampleRate: Int = 48000,
        bitsPerSample: Int = 16
    ) {
        val output = createOutputFile()
        val totalDataSize =
            buffers.size * buffers[0].remaining()  // Assuming all buffers are of equal size and fully filled
        Log.d("WebRTCClient", "totalDataSize: $totalDataSize")
        FileOutputStream(output).use { encoded ->

            // WAVE RIFF header
            writeToOutput(encoded, "RIFF")
            writeToOutput(encoded, 36 + totalDataSize) // Add total data size to header size
            writeToOutput(encoded, "WAVE")

            // SUB CHUNK 1 (FORMAT)
            writeToOutput(encoded, "fmt ")
            writeToOutput(encoded, 16)  // Fixed size for PCM header
            writeToOutput(encoded, 1.toShort())  // Audio format 1 for PCM
            writeToOutput(encoded, channelCount.toShort())  // Mono
            writeToOutput(encoded, sampleRate)  // 48000 Hz
            writeToOutput(encoded, sampleRate * channelCount * bitsPerSample / 8)  // Byte rate
            writeToOutput(encoded, (channelCount * bitsPerSample / 8).toShort())  // Block align
            writeToOutput(encoded, bitsPerSample.toShort())  // 16 bits per sample

            // SUB CHUNK 2 (AUDIO DATA)
            writeToOutput(encoded, "data")
            writeToOutput(encoded, totalDataSize)

            // Write all ByteBuffer data to output
            buffers.forEach { buffer ->
                val data = ByteArray(buffer.remaining())
                buffer.get(data)
                encoded.write(data)
            }
        }
    }

    @Throws(IOException::class)
    fun writeToOutput(output: OutputStream, data: String) {
        for (element in data) {
            output.write(element.code)
        }
    }

    @Throws(IOException::class)
    fun writeToOutput(output: OutputStream, data: Int) {
        output.write(data shr 0)
        output.write(data shr 8)
        output.write(data shr 16)
        output.write(data shr 24)
    }

    @Throws(IOException::class)
    fun writeToOutput(output: OutputStream, data: Short) {
        output.write(data.toInt() shr 0)
        output.write(data.toInt() shr 8)
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
}