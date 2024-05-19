package com.sergiolopez.voicecalltranslator.feature.call.data.mapper

import com.sergiolopez.voicecalltranslator.feature.call.domain.model.OpenAiSyntheticVoice
import com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model.SyntheticVoiceOption
import javax.inject.Inject

class OpenAiSyntheticVoiceMapper @Inject constructor() {

    fun mapUserDatabaseToUserData(syntheticVoiceOption: SyntheticVoiceOption?): OpenAiSyntheticVoice {
        return when (syntheticVoiceOption) {
            SyntheticVoiceOption.MALE -> OpenAiSyntheticVoice.MALE_ONYX
            SyntheticVoiceOption.FEMALE -> OpenAiSyntheticVoice.FEMALE_ALOY
            null -> OpenAiSyntheticVoice.MALE_ONYX
        }
    }
}