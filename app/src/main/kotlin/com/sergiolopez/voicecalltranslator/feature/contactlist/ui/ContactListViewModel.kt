package com.sergiolopez.voicecalltranslator.feature.contactlist.ui

import androidx.lifecycle.viewModelScope
import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.usecase.SaveUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val saveUserUseCase: SaveUserUseCase
) : VoiceCallTranslatorViewModel() {

    init {
        viewModelScope.launch(Dispatchers.IO) {
            //saveUserUseCase.invoke(User("test1", "test2", "test3", "test4"))
        }
    }

    enum class LoginUiState {
        LOADING,
        LOGGED,
        CONTINUE,
        ERROR
    }
}