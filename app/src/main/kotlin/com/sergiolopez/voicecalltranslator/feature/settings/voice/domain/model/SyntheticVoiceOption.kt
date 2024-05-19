package com.sergiolopez.voicecalltranslator.feature.settings.voice.domain.model

import android.content.Context
import com.sergiolopez.voicecalltranslator.R

enum class SyntheticVoiceOption(val nameValue: Int) {
    MALE(R.string.male),
    FEMALE(R.string.female);

    companion object {
        fun getSyntheticVoiceEnum(context: Context, text: String): SyntheticVoiceOption {
            return when (text.lowercase()) {
                context.getString(MALE.nameValue).lowercase() -> {
                    MALE
                }

                context.getString(FEMALE.nameValue).lowercase() -> {
                    FEMALE
                }

                else -> {
                    MALE
                }
            }
        }
    }
}