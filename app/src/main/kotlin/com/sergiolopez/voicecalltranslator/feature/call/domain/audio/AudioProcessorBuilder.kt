package com.sergiolopez.voicecalltranslator.feature.call.domain.audio

import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.OPEN_AI_SAMPLE_RATE
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_LOGS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.Queue

object AudioProcessorBuilder {

    // WebRTC params configuration for PCM audio
    private const val CHANNEL_COUNT = 1
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
                    writeToOutput(encoded, OPEN_AI_SAMPLE_RATE) // OPEN_AI_SAMPLE_RATE // 24000 Hz
                    writeToOutput(
                        encoded,
                        OPEN_AI_SAMPLE_RATE * CHANNEL_COUNT * BITS_PER_SAMPLE / 8
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
            Log.d(VCT_LOGS, "createWavFileFromByteBufferList ${exception.message}")
        }

        return result
    }

    private fun writeToOutput(encoded: FileOutputStream, value: String) {
        encoded.write(value.toByteArray())
    }

    private fun writeToOutput(encoded: FileOutputStream, value: Int) {
        encoded.write(
            byteArrayOf(
                (value and 0xFF).toByte(),
                (value shr 8 and 0xFF).toByte(),
                (value shr 16 and 0xFF).toByte(),
                (value shr 24 and 0xFF).toByte()
            )
        )
    }

    private fun writeToOutput(encoded: FileOutputStream, value: Short) {
        encoded.write(
            byteArrayOf(
                (value.toInt() and 0xFF).toByte(),
                (value.toInt() shr 8 and 0xFF).toByte()
            )
        )
    }

    // Use this method to save the ByteArray Wav response from OpenAI speech
    suspend fun createWavFileFromByteArray(
        byteArray: ByteArray,
        output: File
    ): Boolean {
        var result = false
        try {
            val totalDataSize = byteArray.size

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
                    writeToOutput(encoded, OPEN_AI_SAMPLE_RATE) // OPEN_AI_SAMPLE_RATE // 24000 Hz
                    writeToOutput(
                        encoded,
                        OPEN_AI_SAMPLE_RATE * CHANNEL_COUNT * BITS_PER_SAMPLE / 8
                    )  // Byte rate
                    writeToOutput(
                        encoded,
                        2.toShort()  // Block align
                    )
                    writeToOutput(encoded, BITS_PER_SAMPLE.toShort())  // 16 bits per sample

                    // SUB CHUNK 2 (AUDIO DATA)
                    writeToOutput(encoded, "data")
                    writeToOutput(encoded, totalDataSize)

                    // Write ByteArray data to output
                    encoded.write(byteArray)

                    result = true
                }
            }

        } catch (exception: Exception) {
            Log.d(VCT_LOGS, "createWavFileFromByteArray ${exception.message}")
        }

        return result
    }

    fun fillBufferQueueFromWavByteArray(
        rawAudioBytes: ByteArray,
        bufferMiddleQueue: Queue<ByteBuffer>
    ) {
        val sampleSizeBytes = 2
        val samplesPer10ms = 240
        val bytesPer10ms = samplesPer10ms * sampleSizeBytes

        var startIndex = 0

        while (startIndex < rawAudioBytes.size) {
            val endIndex = startIndex + bytesPer10ms
            if (endIndex <= rawAudioBytes.size) {
                bufferMiddleQueue.add(ByteBuffer.wrap(rawAudioBytes, startIndex, bytesPer10ms))
            }
            startIndex += bytesPer10ms
        }
    }

    // Extract Wav structure from ByteArray
    fun getWavFileStructure(
        byteArray: ByteArray
    ) {
        val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(20) // Jump first RIFF header 20 bytes

        val audioFormat = buffer.short
        val numChannels = buffer.short
        val sampleRate = buffer.int
        val byteRate = buffer.int
        val blockAlign = buffer.short
        val bitsPerSample = buffer.short

        Log.d("AudioProcessor", "Audio Format: $audioFormat")
        Log.d("AudioProcessor", "Channels: $numChannels")
        Log.d("AudioProcessor", "Sample Rate: $sampleRate")
        Log.d("AudioProcessor", "Byte Rate: $byteRate")
        Log.d("AudioProcessor", "Block Align: $blockAlign")
        Log.d("AudioProcessor", "Bits Per Sample: $bitsPerSample")
    }
}