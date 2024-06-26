package com.sergiolopez.voicecalltranslator.feature.common.data.repository

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sergiolopez.voicecalltranslator.feature.common.data.mapper.ResultOperation.performSimpleApiOperation
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.UserStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthRepository @Inject constructor() {

    private val _currentUser: MutableStateFlow<User?> = MutableStateFlow(null)
    val currentUser: StateFlow<User?>
        get() = _currentUser.asStateFlow()

    init {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            auth.currentUser?.let {
                val user = User(
                    id = it.uid,
                    email = it.email ?: "",
                    creationDate = it.metadata?.creationTimestamp.toString(),
                    lastLogin = it.metadata?.lastSignInTimestamp.toString(),
                    uuid = it.tenantId.orEmpty(),
                    status = UserStatus.ONLINE
                )
                _currentUser.value = user
            }
        }
        Firebase.auth.addAuthStateListener(listener)
    }

    // TODO : Add use cases layer to access the repository

    fun isUserLogged(): Boolean {
        return Firebase.auth.currentUser != null
    }

    suspend fun signUp(email: String, password: String): Boolean {
        return performSimpleApiOperation {
            Firebase.auth.createUserWithEmailAndPassword(email, password).await()
        }
    }

    suspend fun deleteAccount(): Boolean {
        return performSimpleApiOperation {
            _currentUser.value = null
            Firebase.auth.currentUser?.delete()?.await()
        }
    }

    suspend fun login(email: String, password: String): Boolean {
        return performSimpleApiOperation {
            Firebase.auth.signInWithEmailAndPassword(email, password).await()
        }
    }

    suspend fun logout(): Boolean {
        return performSimpleApiOperation {
            _currentUser.value = null
            Firebase.auth.signOut()
        }
    }
}


