package com.sergiolopez.voicecalltranslator.feature.common.domain.subscriber

import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CurrentUserSubscriber @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {

    private val _currentUserState = MutableStateFlow<User>(User.UserNoData)
    val currentUserState: StateFlow<User> = _currentUserState

    suspend fun subscribe() {
        firebaseAuthRepository.currentUser.collect { user ->
            if (user is User.UserData) {
                _currentUserState.value = User.UserData(
                    id = user.id,
                    email = user.email,
                    creationDate = user.creationDate,
                    lastLogin = user.lastLogin,
                    uuid = user.uuid,
                    status = user.status
                )
            }
        }
    }
}