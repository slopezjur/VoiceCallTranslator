package com.sergiolopez.voicecalltranslator.signup.domain.usecase

import com.sergiolopez.voicecalltranslator.common.data.FirebaseRepository
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