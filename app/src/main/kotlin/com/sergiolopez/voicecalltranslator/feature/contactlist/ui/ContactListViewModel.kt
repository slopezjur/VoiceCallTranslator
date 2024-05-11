package com.sergiolopez.voicecalltranslator.feature.contactlist.ui

import com.sergiolopez.voicecalltranslator.VoiceCallTranslatorViewModel
import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.usecase.GetUserListUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ContactListViewModel @Inject constructor(
    private val getUserListUseCase: GetUserListUseCase,
    private val firebaseAuthRepository: FirebaseAuthRepository
) : VoiceCallTranslatorViewModel() {

    private val _contactListUiState = MutableStateFlow(ContactListUiState.CONTINUE)
    val contactListUiState: StateFlow<ContactListUiState> = _contactListUiState.asStateFlow()

    private val _userList = MutableStateFlow(emptyList<User>())
    val userList: StateFlow<List<User>> = _userList.asStateFlow()

    init {
        _contactListUiState.value = ContactListUiState.LOADING
        launchCatching {
            firebaseAuthRepository.currentUser.collect { user ->
                if (user is User) {
                    launchCatching {
                        val result =
                            getUserListUseCase.invoke(
                                userId = user.id
                            )
                        if (result.isSuccess) {
                            result.getOrNull()?.collect {
                                _userList.value = it
                            }
                            _contactListUiState.value = ContactListUiState.CONTINUE
                        } else {
                            _contactListUiState.value = ContactListUiState.ERROR
                        }
                    }
                }
            }
        }
    }

    enum class ContactListUiState {
        LOADING,
        CONTINUE,
        ERROR
    }
}