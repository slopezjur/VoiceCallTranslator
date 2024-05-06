package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.nio.ByteBuffer

object WavFileBuilder {

    // WebRTC params configuration for PCM audio
    private const val CHANNEL_COUNT = 1
    private const val SAMPLE_RATE = 48000
    private const val BITS_PER_SAMPLE = 16

    suspend fun createWavFileFromByteBufferList(
        byteBufferList: List<ByteBuffer>,
        output: File
    ): Boolean {
        var result = false
        try {

            val totalDataSize =
                byteBufferList.size * byteBufferList[0].remaining()

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
                    writeToOutput(encoded, CHANNEL_COUNT.toShort())  // Mono
                    writeToOutput(encoded, SAMPLE_RATE)  // 48000 Hz
                    writeToOutput(
                        encoded,
                        SAMPLE_RATE * CHANNEL_COUNT * BITS_PER_SAMPLE / 8
                    )  // Byte rate
                    writeToOutput(
                        encoded,
                        (CHANNEL_COUNT * BITS_PER_SAMPLE / 8).toShort()
                    )  // Block align
                    writeToOutput(encoded, BITS_PER_SAMPLE.toShort())  // 16 bits per sample

                    // SUB CHUNK 2 (AUDIO DATA)
                    writeToOutput(encoded, "data")
                    writeToOutput(encoded, totalDataSize)

                    // Write all ByteBuffer data to output
                    byteBufferList.forEach { buffer ->
                        val data = ByteArray(buffer.remaining())
                        buffer.get(data)
                        encoded.write(data)
                    }

                    result = true
                }
            }

        } catch (exception: Exception) {
            Log.d("VCT_LOGS", "createWavFileFromByteBufferList ${exception.message}")
            result = false
        }

        return result
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