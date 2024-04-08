package com.sergiolopez.voicecalltranslator.app

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.sergiolopez.voicecalltranslator.app.ui.VoiceCallTranslatorTheme

class VoiceCallTranslatorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            VoiceCallTranslatorTheme {
                HelloThere()
            }
        }
    }
}

@Composable
fun HelloThere() {
    Text(
        text = "Hello there, it's me!",
        color = MaterialTheme.colorScheme.primary
    )
}

@Preview
@Composable
fun HelloThereContent() {
    HelloThere()
}