package com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model

import com.sergiolopez.voicecalltranslator.R

enum class SyntheticVoiceOption(val nameValue: Int) {
    NONE(R.string.none),
    MALE(R.string.male),
    FEMALE(R.string.female);

    companion object {
        fun getSyntheticVoiceEnum(text: String): SyntheticVoiceOption {
            return when (text.lowercase()) {
                MALE.name.lowercase() -> {
                    MALE
                }

                FEMALE.name.lowercase() -> {
                    FEMALE
                }

                else -> {
                    NONE
                }
            }
        }
    }
}