package com.sergiolopez.voicecalltranslator.feature.call.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.Message
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.theme.VoiceCallTranslatorPreview
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
internal fun CallScreenConversation(
    modifier: Modifier,
    messages: List<Message>
) {
    val listState = rememberLazyListState()

    LazyColumn(
        modifier = modifier
            .padding(horizontal = 8.dp)
            .fillMaxSize(),
        state = listState,
        reverseLayout = true
    ) {
        items(
            count = messages.size,
            key = { messages[it].timestamp }) { index ->
            val message = messages[messages.size - 1 - index]
            CallScreenConversationMessage(
                modifier = modifier,
                message = message
            )
        }
    }
}

@Composable
private fun CallScreenConversationMessage(
    modifier: Modifier,
    message: Message
) {
    val backgroundColor =
        if (message.isSent) MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val textColor =
        if (message.isSent) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
    val alignment = if (message.isSent) Alignment.End else Alignment.Start

    Column(
        horizontalAlignment = alignment,
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = 8.dp,
                bottom = 8.dp,
                start = if (message.isSent) 64.dp else 0.dp,
                end = if (!message.isSent) 64.dp else 0.dp
            )
    ) {
        Box(
            modifier = modifier
                .clip(RoundedCornerShape(8.dp))
                .background(backgroundColor)
                .padding(8.dp)
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = message.text, fontSize = 16.sp, color = textColor)
                Row {
                    Text(
                        text = SimpleDateFormat(
                            "HH:mm",
                            Locale.getDefault()
                        ).format(Date(message.timestamp)),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Light,
                        color = textColor
                    )
                }
            }
        }
    }
}

@PreviewLightDark
@Composable
fun CallScreenConversationPreview() {
    VoiceCallTranslatorPreview {
        CallScreenConversation(
            modifier = Modifier,
            messages = Dummy.messages
        )
    }
}

@PreviewLightDark
@Composable
fun CallScreenConversationOneMessagePreview() {
    VoiceCallTranslatorPreview {
        CallScreenConversation(
            modifier = Modifier,
            messages = listOf(Dummy.message)
        )
    }
}