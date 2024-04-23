package com.sergiolopez.voicecalltranslator.feature.login.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.domain.service.FirebaseAuthService
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val firebaseAuthService: FirebaseAuthService
) {

    suspend operator fun invoke(email: String, password: String) {
        return firebaseAuthService.login(
            email = email,
            password = password
        )
    }
}