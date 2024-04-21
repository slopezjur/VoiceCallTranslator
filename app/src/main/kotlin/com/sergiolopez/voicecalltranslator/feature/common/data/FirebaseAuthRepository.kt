package com.sergiolopez.voicecalltranslator.feature.common.data

import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.UserStatus
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthRepository @Inject constructor() {

    val currentUser: Flow<User>
        get() = callbackFlow {
            val listener =
                FirebaseAuth.AuthStateListener { auth ->
                    this.trySend(auth.currentUser?.let {
                        User.Logged(
                            id = it.uid,
                            email = it.email ?: "",
                            creationDate = it.metadata?.creationTimestamp.toString(),
                            lastLogin = it.metadata?.lastSignInTimestamp.toString(),
                            uuid = it.tenantId.orEmpty(),
                            status = UserStatus.ONLINE
                        )
                    } ?: User.None)
                }
            Firebase.auth.addAuthStateListener(listener)
            awaitClose { Firebase.auth.removeAuthStateListener(listener) }
        }

    suspend fun login(email: String, password: String) {
        Firebase.auth.signInWithEmailAndPassword(email, password).await()
    }

    suspend fun signUp(email: String, password: String) {
        Firebase.auth.createUserWithEmailAndPassword(email, password).await()
    }
}