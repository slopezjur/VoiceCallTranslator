package com.sergiolopez.voicecalltranslator.feature.signup.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService
) {

    suspend operator fun invoke(email: String, password: String) {
        return firebaseAuthService.signUp(
            email = email,
            password = password
        )
    }
}