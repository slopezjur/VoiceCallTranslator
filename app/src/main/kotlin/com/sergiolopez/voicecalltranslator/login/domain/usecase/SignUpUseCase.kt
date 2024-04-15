package com.sergiolopez.voicecalltranslator.login.domain.usecase

import com.sergiolopez.voicecalltranslator.login.data.LoginRepository
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        return loginRepository.signUp(
            email = email,
            password = password
        )
    }
}