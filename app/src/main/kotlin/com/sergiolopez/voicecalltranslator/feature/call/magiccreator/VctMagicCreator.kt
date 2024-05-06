package com.sergiolopez.voicecalltranslator.feature.call.magiccreator

import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import com.sergiolopez.voicecalltranslator.VctApiKeys
import com.sergiolopez.voicecalltranslator.feature.call.data.mapper.OpenAiSyntheticVoiceMapper
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetSyntheticVoiceOptionUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import okio.source
import java.io.File
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VctMagicCreator @Inject constructor(
    private val getSyntheticVoiceOptionUseCase: GetSyntheticVoiceOptionUseCase,
    private val firebaseAuthService: FirebaseAuthService,
    private val openAiSyntheticVoiceMapper: OpenAiSyntheticVoiceMapper
) {
    private var openAI = OpenAI(
        token = VctApiKeys.OPEN_AI_API_KEY,
        logging = LoggingConfig(LogLevel.All)
    )

    private var openAiSyntheticVoice: OpenAiSyntheticVoice? = null

    private val chatMessageHistoryQueue: Queue<ChatMessage> = LinkedList()

    private val defaultChatMessageList = listOf(
        ChatMessage(
            role = ChatRole.System,
            content = "You are a really helpful assistant translator and you have to translate text between different languages taking into account also the context of the current conversation which you can check by looking at the content of “HISTORY_TRANSLATION_KEY” for every message."
        )
    )

    suspend fun initializeVctMagicCreator() {
        firebaseAuthService.currentUser.collect { user ->
            user?.id?.let { userId ->
                openAiSyntheticVoice = openAiSyntheticVoiceMapper.mapUserDatabaseToUserData(
                    getSyntheticVoiceOptionUseCase.invoke(
                        userId = userId
                    )
                )
            }
        }
    }

    suspend fun speechToText(outputFile: File): String {
        // TODO : If destiny language is English we can directly use TranslationRequest
        val transcriptionRequest = TranscriptionRequest(
            audio = FileSource(
                name = OpenAiParams.AUDIO_WAV_FILE,
                source = outputFile.source(),
            ),
            model = ModelId(OpenAiParams.TRANSCRIPTION_WHISPER_MODEL),
            language = OpenAiParams.LANGUAGE_ES
        )

        val transcription = openAI.transcription(
            request = transcriptionRequest
        )

        return transcription.text
    }

    suspend fun translation(textToTranslate: String): String? {
        // TODO - Use global configuration
        val originLanguage = "Spanish"
        val destinationLanguage = "English"
        //

        val chatMessageForHistory = ChatMessage(
            role = ChatRole.User,
            content = "HISTORY_TRANSLATION_KEY='$textToTranslate'"
        )

        chatMessageHistoryQueue.add(chatMessageForHistory)

        val currentChatMessage = ChatMessage(
            role = ChatRole.User,
            content = "Translate '$textToTranslate' from $originLanguage to $destinationLanguage. Answer only with the translation."
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(OpenAiParams.TRANSLATION_GPT_3_5_TURBO),
            messages = defaultChatMessageList + chatMessageHistoryQueue + listOf(currentChatMessage)
        )

        val translation = openAI.chatCompletion(
            request = chatCompletionRequest
        )

        if (chatMessageHistoryQueue.size >= CHAT_MESSAGE_HISTORY_LIMIT) {
            chatMessageHistoryQueue.poll()
        }

        return translation.choices.firstOrNull()?.message?.content
    }

    suspend fun textToSpeech(textToSpeech: String): ByteArray {

        val speechRequest = SpeechRequest(
            model = ModelId(OpenAiParams.SPEECH_TTS_1),
            input = textToSpeech,
            voice = OpenAiSyntheticVoice.getOpenAiVoice(
                openAiSyntheticVoice = openAiSyntheticVoice
            )
        )

        val rawAudio = openAI.speech(speechRequest)

        return rawAudio
    }

    companion object {
        private const val CHAT_MESSAGE_HISTORY_LIMIT = 20
    }
}