package com.sergiolopez.voicecalltranslator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

class VoiceCallTranslatorActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HelloThere()
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