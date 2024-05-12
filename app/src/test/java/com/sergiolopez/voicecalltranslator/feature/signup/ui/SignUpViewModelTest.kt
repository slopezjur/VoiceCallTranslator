package com.sergiolopez.voicecalltranslator.feature.signup.ui

import com.sergiolopez.voicecalltranslator.feature.common.data.repository.FirebaseAuthRepository
import com.sergiolopez.voicecalltranslator.feature.common.domain.SaveUserUseCase
import com.sergiolopez.voicecalltranslator.feature.common.utils.Dummy
import com.sergiolopez.voicecalltranslator.feature.contactlist.domain.model.User
import com.sergiolopez.voicecalltranslator.feature.signup.domain.usecase.SignUpUseCase
import com.sergiolopez.voicecalltranslator.testing.DummyTesting
import com.sergiolopez.voicecalltranslator.testing.MainDispatcherRule
import io.mockk.Ordering
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SignUpViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val signUpUseCase: SignUpUseCase = mockk()
    private val firebaseAuthRepository: FirebaseAuthRepository = mockk()
    private val saveUserUseCase: SaveUserUseCase = mockk()

    private val email = DummyTesting.email
    private val password = DummyTesting.password
    private val confirmedPassword = "ComplexPassword"
    private val wrongConfirmedPassword = "Complex-Password"

    private lateinit var signUpViewModel: SignUpViewModel

    @Before
    fun setup() {
        signUpViewModel = SignUpViewModel(
            signUpUseCase = signUpUseCase,
            firebaseAuthRepository = firebaseAuthRepository,
            saveUserUseCase = saveUserUseCase
        )
    }

    @Test
    fun initialLoading() = runTest {
        assertEquals(SignUpViewModel.SignUpUiState.CONTINUE, signUpViewModel.signUpUiState.value)
        assertEquals("", signUpViewModel.emailState.value)
        assertEquals("", signUpViewModel.passwordState.value)
        assertEquals("", signUpViewModel.confirmPasswordState.value)
        assertEquals(false, signUpViewModel.isPasswordError.value)
    }

    @Test
    fun resetUiState() = runTest {
        assertEquals(SignUpViewModel.SignUpUiState.CONTINUE, signUpViewModel.signUpUiState.value)

        signUpViewModel.resetUiState()

        assertEquals(SignUpViewModel.SignUpUiState.CONTINUE, signUpViewModel.signUpUiState.value)
    }

    @Test
    fun updateEmail() = runTest {
        assertEquals("", signUpViewModel.emailState.value)

        signUpViewModel.updateEmail(email)

        assertEquals(email, signUpViewModel.emailState.value)
    }

    @Test
    fun updatePassword() = runTest {
        assertEquals("", signUpViewModel.passwordState.value)

        signUpViewModel.updatePassword(password)

        assertEquals(password, signUpViewModel.passwordState.value)
    }

    @Test
    fun updateConfirmPassword() = runTest {
        assertEquals("", signUpViewModel.confirmPasswordState.value)

        signUpViewModel.updateConfirmPassword(confirmedPassword)

        assertEquals(confirmedPassword, signUpViewModel.confirmPasswordState.value)
    }

    @Test
    fun resetPasswordDifferent() = runTest {
        assertEquals(false, signUpViewModel.isPasswordError.value)

        signUpViewModel.resetPasswordDifferent()

        assertEquals(false, signUpViewModel.isPasswordError.value)
    }

    @Test
    fun onSignUpClickWhenPasswordAndConfirmPasswordAreDifferent() = runTest {
        signUpViewModel.updatePassword(password)
        signUpViewModel.updateConfirmPassword(wrongConfirmedPassword)

        assertEquals(false, signUpViewModel.isPasswordError.value)

        signUpViewModel.onSignUpClick {}

        assertEquals(true, signUpViewModel.isPasswordError.value)
        coVerify(exactly = 0) {
            signUpUseCase.invoke(any(), any())
            saveUserUseCase.invoke(any())
        }
    }

    @Test
    fun onSignUpClickWhenPasswordAndConfirmPasswordAreEquals() = runTest {
        val user = Dummy.user
        val userFlow = MutableStateFlow<User?>(user)
        val signUpUiStates = mutableListOf<SignUpViewModel.SignUpUiState>()

        coEvery { firebaseAuthRepository.currentUser } returns userFlow
        coEvery { signUpUseCase.invoke(email, password) } returns true
        coEvery { saveUserUseCase.invoke(user) } returns true

        val signUpUiStateObserver =
            launch(UnconfinedTestDispatcher()) {
                signUpViewModel.signUpUiState.collect {
                    signUpUiStates.add(it)
                }
            }

        signUpViewModel.updateEmail(email)
        signUpViewModel.updatePassword(password)
        signUpViewModel.updateConfirmPassword(confirmedPassword)

        assertEquals(false, signUpViewModel.isPasswordError.value)

        signUpViewModel.onSignUpClick {}

        assertEquals(false, signUpViewModel.isPasswordError.value)
        assertEquals(SignUpViewModel.SignUpUiState.CONTINUE, signUpUiStates[0])
        assertEquals(SignUpViewModel.SignUpUiState.LOADING, signUpUiStates[1])
        assertEquals(SignUpViewModel.SignUpUiState.CONTINUE, signUpUiStates[2])

        coVerify(ordering = Ordering.SEQUENCE) {
            signUpUseCase.invoke(email, password)
            saveUserUseCase.invoke(user)
        }

        signUpUiStateObserver.cancel()
    }

    @Test
    fun onSignUpClickWhenSaveUserError() = runTest {
        val user = Dummy.user
        val userFlow = MutableStateFlow<User?>(user)
        val signUpUiStates = mutableListOf<SignUpViewModel.SignUpUiState>()

        coEvery { firebaseAuthRepository.currentUser } returns userFlow
        coEvery { signUpUseCase.invoke(email, password) } returns true
        coEvery { saveUserUseCase.invoke(user) } returns false

        val signUpUiStateObserver =
            launch(UnconfinedTestDispatcher()) {
                signUpViewModel.signUpUiState.collect {
                    signUpUiStates.add(it)
                }
            }

        signUpViewModel.updateEmail(email)
        signUpViewModel.updatePassword(password)
        signUpViewModel.updateConfirmPassword(confirmedPassword)

        assertEquals(false, signUpViewModel.isPasswordError.value)

        signUpViewModel.onSignUpClick {}

        assertEquals(false, signUpViewModel.isPasswordError.value)
        assertEquals(SignUpViewModel.SignUpUiState.CONTINUE, signUpUiStates[0])
        assertEquals(SignUpViewModel.SignUpUiState.LOADING, signUpUiStates[1])
        assertEquals(SignUpViewModel.SignUpUiState.ERROR, signUpUiStates[2])

        coVerify(ordering = Ordering.SEQUENCE) {
            signUpUseCase.invoke(email, password)
            saveUserUseCase.invoke(user)
        }

        signUpUiStateObserver.cancel()
    }

    @Test
    fun onSignUpClickWhenSignUpUseCaseError() = runTest {
        val user = Dummy.user
        val userFlow = MutableStateFlow<User?>(user)
        val signUpUiStates = mutableListOf<SignUpViewModel.SignUpUiState>()

        coEvery { firebaseAuthRepository.currentUser } returns userFlow
        coEvery { signUpUseCase.invoke(email, password) } returns false

        val signUpUiStateObserver =
            launch(UnconfinedTestDispatcher()) {
                signUpViewModel.signUpUiState.collect {
                    signUpUiStates.add(it)
                }
            }

        signUpViewModel.updateEmail(email)
        signUpViewModel.updatePassword(password)
        signUpViewModel.updateConfirmPassword(confirmedPassword)

        assertEquals(false, signUpViewModel.isPasswordError.value)

        signUpViewModel.onSignUpClick {}

        assertEquals(false, signUpViewModel.isPasswordError.value)
        assertEquals(SignUpViewModel.SignUpUiState.CONTINUE, signUpUiStates[0])
        assertEquals(SignUpViewModel.SignUpUiState.LOADING, signUpUiStates[1])
        assertEquals(SignUpViewModel.SignUpUiState.ERROR, signUpUiStates[2])

        coVerify {
            signUpUseCase.invoke(email, password)
        }
        coVerify(exactly = 0) {
            saveUserUseCase.invoke(any())
        }

        signUpUiStateObserver.cancel()
    }
}