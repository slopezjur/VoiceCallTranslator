package com.sergiolopez.voicecalltranslator.login.domain.subscriber

import com.sergiolopez.voicecalltranslator.login.data.LoginRepository
import com.sergiolopez.voicecalltranslator.login.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class CurrentUserSubscriber @Inject constructor(
    private val loginRepository: LoginRepository
) {

    private val _currentUserState = MutableStateFlow<User?>(null)
    val currentUserState: StateFlow<User?> = _currentUserState

    suspend fun subscribe() {
        loginRepository.currentUser.collect { user ->
            if (user != null) {
                _currentUserState.value = User(
                    id = user.id,
                    creationDate = user.creationDate,
                    lastLogin = user.lastLogin,
                    uuid = user.uuid
                )
            }
        }
    }
}