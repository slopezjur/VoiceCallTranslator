package com.sergiolopez.voicecalltranslator.login.domain.usecase

import com.sergiolopez.voicecalltranslator.login.data.LoginRepository
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val loginRepository: LoginRepository
) {

    suspend operator fun invoke(email: String, password: String) {
        return loginRepository.login(
            email = email,
            password = password
        )
    }
}