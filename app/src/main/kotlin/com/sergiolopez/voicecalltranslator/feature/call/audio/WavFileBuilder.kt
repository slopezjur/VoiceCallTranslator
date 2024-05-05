package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer

object WavFileBuilder {

    suspend fun createWavFileFromByteBufferList(
        buffers: List<ByteBuffer>,
        channelCount: Int = 1,
        sampleRate: Int = 48000,
        bitsPerSample: Int = 16,
        output: File
    ) {
        val totalDataSize =
            buffers.size * buffers[0].remaining()  // Assuming all buffers are of equal size and fully filled
        Log.d("AudioProcessor", "totalDataSize: $totalDataSize")
        withContext(Dispatchers.IO) {
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
            Log.d("AudioProcessor", "buffers: ${buffers.size}")
        }
    }

    private fun writeToOutput(output: OutputStream, data: String) {
        for (element in data) {
            output.write(element.code)
        }
    }

    private fun writeToOutput(output: OutputStream, data: Int) {
        output.write(data shr 0)
        output.write(data shr 8)
        output.write(data shr 16)
        output.write(data shr 24)
    }

    private fun writeToOutput(output: OutputStream, data: Short) {
        output.write(data.toInt() shr 0)
        output.write(data.toInt() shr 8)
    }
}