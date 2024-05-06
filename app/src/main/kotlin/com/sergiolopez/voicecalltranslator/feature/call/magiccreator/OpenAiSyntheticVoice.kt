package com.sergiolopez.voicecalltranslator.feature.call.magiccreator

import com.aallam.openai.api.audio.Voice

enum class OpenAiSyntheticVoice {
    NONE,
    MALE_ONYX,
    FEMALE_ALOY;

    companion object {
        fun getOpenAiVoice(openAiSyntheticVoice: OpenAiSyntheticVoice?): Voice {
            return when (openAiSyntheticVoice) {
                MALE_ONYX -> {
                    Voice.Onyx
                }

                FEMALE_ALOY -> {
                    Voice.Alloy
                }

                else -> {
                    Voice.Onyx
                }
            }
        }
    }
}