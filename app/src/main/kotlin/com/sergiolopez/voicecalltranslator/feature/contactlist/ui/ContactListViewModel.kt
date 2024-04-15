package com.sergiolopez.voicecalltranslator.feature.contactlist.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
) : VoiceCallTranslatorViewModel() {



    enum class LoginUiState {
        LOADING,
        LOGGED,
        CONTINUE,
        ERROR
    }
}