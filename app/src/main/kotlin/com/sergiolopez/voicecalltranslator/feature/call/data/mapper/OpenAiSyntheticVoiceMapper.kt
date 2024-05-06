package com.sergiolopez.voicecalltranslator.feature.call.data.mapper

import com.sergiolopez.voicecalltranslator.feature.call.magiccreator.OpenAiSyntheticVoice
import com.sergiolopez.voicecalltranslator.feature.settings.voice.ui.SyntheticVoiceOption
import javax.inject.Inject

class OpenAiSyntheticVoiceMapper @Inject constructor() {

    fun mapUserDatabaseToUserData(syntheticVoiceOption: SyntheticVoiceOption?): OpenAiSyntheticVoice {
        return when (syntheticVoiceOption) {
            SyntheticVoiceOption.NONE -> OpenAiSyntheticVoice.NONE
            SyntheticVoiceOption.MALE -> OpenAiSyntheticVoice.MALE_ONYX
            SyntheticVoiceOption.FEMALE -> OpenAiSyntheticVoice.FEMALE_ALOY
            null -> OpenAiSyntheticVoice.NONE
        }
    }
}