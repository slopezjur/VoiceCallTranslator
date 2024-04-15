package com.sergiolopez.voicecalltranslator.feature.signup.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.FirebaseRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val firebaseRepository: FirebaseRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        return firebaseRepository.signUp(
            email = email,
            password = password
        )
    }
}