package com.sergiolopez.voicecalltranslator.feature.settings.voice.ui

import com.sergiolopez.voicecalltranslator.R

enum class SyntheticVoiceOption(val nameValue: Int) {
    NONE(R.string.none),
    MALE(R.string.male),
    FEMALE(R.string.female);

    companion object {
        fun getSyntheticVoiceEnum(text: String): SyntheticVoiceOption {
            return when (text) {
                MALE.name -> {
                    MALE
                }

                FEMALE.name -> {
                    FEMALE
                }

                else -> {
                    NONE
                }
            }
        }
    }
}