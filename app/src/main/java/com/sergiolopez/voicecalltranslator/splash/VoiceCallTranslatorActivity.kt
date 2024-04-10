package com.sergiolopez.voicecalltranslator.splash

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
//import com.sergiolopez.voicecalltranslator.login.ui.login.LoginViewModel
import com.sergiolopez.voicecalltranslator.splash.ui.VoiceCallTranslatorTheme
//import dagger.hilt.android.AndroidEntryPoint

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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Hello there, it's me!",
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Preview
@Composable
fun HelloThereContent() {
    HelloThere()
}