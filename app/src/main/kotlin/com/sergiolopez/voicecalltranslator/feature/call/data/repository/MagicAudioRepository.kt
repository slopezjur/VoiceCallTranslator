package com.sergiolopez.voicecalltranslator.feature.call.data.repository

import android.util.Log
import com.aallam.openai.api.audio.AudioResponseFormat
import com.aallam.openai.api.audio.SpeechRequest
import com.aallam.openai.api.audio.SpeechResponseFormat
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
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.OpenAiSyntheticVoice
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetRawAudioByteArrayUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetSyntheticVoiceOptionUseCase
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.SaveRawAudioByteArrayUseCase
import com.sergiolopez.voicecalltranslator.feature.call.magiccreator.OpenAiParams
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName
import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import okio.source
import java.io.File
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MagicAudioRepository @Inject constructor(
    private val getSyntheticVoiceOptionUseCase: GetSyntheticVoiceOptionUseCase,
    private val firebaseAuthService: FirebaseAuthService,
    private val openAiSyntheticVoiceMapper: OpenAiSyntheticVoiceMapper,
    private val saveRawAudioByteArrayUseCase: SaveRawAudioByteArrayUseCase,
    private val getRawAudioByteArrayUseCase: GetRawAudioByteArrayUseCase
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
            content = SYSTEM_ROLE_PROMPT
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

    suspend fun speechToText(
        outputFile: File
    ): String {
        // TODO : If destiny language is English, we can directly use TranslationRequest

        // The queue it should never contain null values for the content
        val filteredMessages = chatMessageHistoryQueue.mapNotNull { it.content }
        val chatMessageHistory = if (filteredMessages.isEmpty()) {
            null
        } else {
            filteredMessages.joinToString(separator = " ")
        }

        Log.d(VctGlobalName.VCT_MAGIC, "chatMessageHistoryQueue: $chatMessageHistory")

        val transcriptionRequest = TranscriptionRequest(
            audio = FileSource(
                name = OpenAiParams.AUDIO_WAV_FILE,
                source = outputFile.source(),
            ),
            model = ModelId(OpenAiParams.TRANSCRIPTION_WHISPER_MODEL),
            /*prompt = chatMessageHistoryQueue.lastOrNull()?.let {
                it.messageContent.toString()
            },*/
            prompt = chatMessageHistory,
            responseFormat = AudioResponseFormat.Text,
            temperature = WHISPER_TEMPERATURE,
            // TODO : Get from Global configuration
            language = OpenAiParams.LANGUAGE_ES
        )

        val transcription = openAI.transcription(
            request = transcriptionRequest
        )

        return transcription.text
    }

    fun updateCallTranscriptionHistory(
        audioTranscription: String
    ) {
        val chatMessageForHistory = ChatMessage(
            role = ChatRole.User,
            content = audioTranscription
        )

        chatMessageHistoryQueue.add(chatMessageForHistory)

        if (chatMessageHistoryQueue.size >= CHAT_MESSAGE_HISTORY_LIMIT) {
            chatMessageHistoryQueue.poll()
        }
    }

    suspend fun translation(textToTranslate: String): String? {
        // TODO - Use global configuration
        val originLanguage = "Spanish"
        val destinationLanguage = "English"
        //

        val currentChatMessage = ChatMessage(
            role = ChatRole.User,
            content = "Translate '$textToTranslate' from $originLanguage to $destinationLanguage. Answer only with the final translation without adding anything else."
        )

        val chatCompletionRequest = ChatCompletionRequest(
            model = ModelId(OpenAiParams.TRANSLATION_GPT_3_5_TURBO),
            messages = defaultChatMessageList + chatMessageHistoryQueue + listOf(currentChatMessage),
            n = MAX_ANSWERS_PER_REQUEST,
            maxTokens = MAX_TOKENS_PER_REQUEST,
            //toolChoice = ToolChoice.None,
        )

        val translation = openAI.chatCompletion(
            request = chatCompletionRequest
        )

        return translation.choices.firstOrNull()?.message?.content
    }

    suspend fun textToSpeech(textToSpeech: String): ByteArray {

        val speechRequest = SpeechRequest(
            model = ModelId(OpenAiParams.SPEECH_TTS_1),
            input = textToSpeech,
            voice = OpenAiSyntheticVoice.getOpenAiVoice(
                openAiSyntheticVoice = openAiSyntheticVoice
            ),
            responseFormat = SpeechResponseFormat(
                value = WAV_SPEECH_RESPONSE_FORMAT
            )
        )

        //saveRawAudioByteArrayUseCase.invoke(rawAudio)

        return openAI.speech(speechRequest) //getRawAudioByteArrayUseCase.invoke() ?: ByteArray(0)
    }

    companion object {
        private const val SYSTEM_ROLE_PROMPT =
            "You are a really helpful translator that translate text between different " +
                    "languages taking into account also the context of the current conversation, " +
                    "which you can check by looking at the content of every message from the User role."
        private const val CHAT_MESSAGE_HISTORY_LIMIT = 20
        private const val MAX_ANSWERS_PER_REQUEST = 1
        private const val MAX_TOKENS_PER_REQUEST = 40
        private const val WAV_SPEECH_RESPONSE_FORMAT = "wav"
        private const val WHISPER_TEMPERATURE = 0.1
    }
}