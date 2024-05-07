package com.sergiolopez.voicecalltranslator.feature.call.audio

import android.content.Context
import android.os.Build
import android.os.Environment
import android.util.Log
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName.VCT_MAGIC
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object AudioFileManager {

    fun createOutputFile(
        context: Context
    ): File {
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

        Log.d(VCT_MAGIC, "outputFile: ${output.path}")

        return output
    }

    fun deleteFile(filePath: String): Boolean {
        val file = File(filePath)

        if (file.isDirectory) {
            file.listFiles()?.forEach { child ->
                deleteFile(child.path)
            }
        }

        return file.delete()
    }
}