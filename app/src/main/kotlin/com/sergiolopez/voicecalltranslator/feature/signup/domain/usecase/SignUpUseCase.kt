package com.sergiolopez.voicecalltranslator.feature.signup.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseAuthRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val firebaseAuthRepository: FirebaseAuthRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        return firebaseAuthRepository.signUp(
            email = email,
            password = password
        )
    }
}