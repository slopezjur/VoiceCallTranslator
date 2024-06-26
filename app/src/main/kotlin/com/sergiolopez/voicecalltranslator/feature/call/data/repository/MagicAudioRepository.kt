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
import com.sergiolopez.voicecalltranslator.feature.call.data.network.magiccreator.OpenAiParams
import com.sergiolopez.voicecalltranslator.feature.call.domain.model.OpenAiSyntheticVoice
import com.sergiolopez.voicecalltranslator.feature.call.domain.usecase.GetSyntheticVoiceOptionUseCase
import com.sergiolopez.voicecalltranslator.feature.common.domain.VctGlobalName
import com.sergiolopez.voicecalltranslator.feature.common.domain.model.LanguageOption
import com.sergiolopez.voicecalltranslator.feature.common.domain.usecase.GetLanguageOptionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import okio.source
import java.io.File
import java.util.LinkedList
import java.util.Queue
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MagicAudioRepository @Inject constructor(
    private val getSyntheticVoiceOptionUseCase: GetSyntheticVoiceOptionUseCase,
    private val openAiSyntheticVoiceMapper: OpenAiSyntheticVoiceMapper,
    private val getLanguageOptionUseCase: GetLanguageOptionUseCase,
    // For testing purpose
    //private val saveRawAudioByteArrayUseCase: SaveRawAudioByteArrayUseCase,
    //private val getRawAudioByteArrayUseCase: GetRawAudioByteArrayUseCase
) {
    private var openAI = OpenAI(
        token = VctApiKeys.OPEN_AI_API_KEY,
        logging = LoggingConfig(LogLevel.All)
    )

    private var openAiSyntheticVoice: OpenAiSyntheticVoice? = null
    private lateinit var languageOption: LanguageOption
    private lateinit var targetLanguage: String

    private val chatMessageHistoryQueue: Queue<ChatMessage> = LinkedList()

    private val defaultChatMessageList = listOf(
        ChatMessage(
            role = ChatRole.System,
            content = SYSTEM_ROLE_PROMPT
        )
    )

    private var _lastTranscription: MutableStateFlow<String?> = MutableStateFlow(null)
    val lastTranscription: StateFlow<String?>
        get() = _lastTranscription.asStateFlow()

    private var _lastTranslation: MutableStateFlow<String?> = MutableStateFlow(null)
    val lastTranslation: StateFlow<String?>
        get() = _lastTranslation.asStateFlow()

    suspend fun initializeSyntheticVoice(userId: String) {
        openAiSyntheticVoice =
            openAiSyntheticVoiceMapper.mapSyntheticVoiceOptionToOpenAiSyntheticVoice(
                getSyntheticVoiceOptionUseCase.invoke(
                    userId = userId
                )
            )
    }

    suspend fun initializeLanguageOption(userId: String) {
        languageOption = getLanguageOptionUseCase.invoke(
            userId = userId
        )
    }

    suspend fun speechToText(
        outputFile: File
    ): String {
        // TODO : If destiny language is English, we can directly use TranslationRequest
        return runCatching {
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
                language = languageOption.getLocalValue()
            )

            val transcription = openAI.transcription(
                request = transcriptionRequest
            )

            if (transcription.text.isNotBlank()) {
                _lastTranscription.value = transcription.text
            }

            transcription.text
        }.onFailure {
            Log.d(VctGlobalName.VCT_MAGIC, "speechToText: $it: ${it.message}")
        }.getOrDefault("")
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
        return runCatching {
            val currentChatMessage = ChatMessage(
                role = ChatRole.User,
                // TODO: After multiple versions, this prompt is still not perfect...
                content = "Translate from ${languageOption.name} to $targetLanguage answering only with the final translation without adding anything else and if the text is already in English, do not translate it again, just return the text you have received to translate, so this is the text you have to translate: $textToTranslate"
            )

            val chatCompletionRequest = ChatCompletionRequest(
                model = ModelId(OpenAiParams.TRANSLATION_GPT_3_5_TURBO),
                messages = defaultChatMessageList + chatMessageHistoryQueue + listOf(
                    currentChatMessage
                ),
                n = MAX_ANSWERS_PER_REQUEST,
                maxTokens = MAX_TOKENS_PER_REQUEST,
                //toolChoice = ToolChoice.None,
            )

            val translation = openAI.chatCompletion(
                request = chatCompletionRequest
            )

            _lastTranslation.value = translation.choices.firstOrNull()?.message?.content

            translation.choices.firstOrNull()?.message?.content
        }.onFailure {
            Log.d(VctGlobalName.VCT_MAGIC, "translation: $it: ${it.message}")
        }.getOrNull()
    }

    suspend fun textToSpeech(textToSpeech: String): ByteArray {
        return runCatching {
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

            openAI.speech(speechRequest) //getRawAudioByteArrayUseCase.invoke() ?: ByteArray(0)
        }.onFailure {
            Log.d(VctGlobalName.VCT_MAGIC, "textToSpeech: $it: ${it.message}")
        }.getOrDefault(ByteArray(0))
    }

    fun setTargetLanguage(targetLanguage: String) {
        this.targetLanguage = targetLanguage
    }

    fun cleanBuffers() {
        chatMessageHistoryQueue.clear()
        _lastTranscription.value = null
        _lastTranslation.value = null
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