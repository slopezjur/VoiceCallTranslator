package com.sergiolopez.voicecalltranslator.feature.signup.domain.usecase

import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.testing.DummyTesting
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SignUpUseCaseTest {

    private val firebaseAuthRepository = mockk<FirebaseAuthRepository>()

    private val email = DummyTesting.email
    private val password = DummyTesting.password

    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    fun setup() {
        signUpUseCase = SignUpUseCase(
            firebaseAuthRepository = firebaseAuthRepository
        )
    }

    @Test
    fun signUpIsValid() = runTest {
        coEvery {
            firebaseAuthRepository.signUp(email, password)
        } returns true

        assertTrue(signUpUseCase.invoke(email, password))
    }

    @Test
    fun signUpIsNotValid() = runTest {
        coEvery {
            firebaseAuthRepository.signUp(email, password)
        } returns false

        assertFalse(signUpUseCase.invoke(email, password))
    }
}