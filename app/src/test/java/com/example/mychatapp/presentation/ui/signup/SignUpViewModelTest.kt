package com.example.mychatapp.presentation.ui.signup

import app.cash.turbine.test
import com.example.mychatapp.domain.usecase.SignUpUseCase
import com.example.mychatapp.presentation.ui.signin.MainTestDispatcher
import com.example.mychatapp.utils.UIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SignUpViewModelTest{
    private val dispatcher = MainTestDispatcher()
    private lateinit var firestore: FirebaseFirestore
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var signUpViewModel: SignUpViewModel
    private lateinit var signUpUseCase: SignUpUseCase

    @Before
    fun setup(){
        signUpUseCase = mockk()
        firestore = mockk()
        firebaseAuth = mockk()
        signUpViewModel = SignUpViewModel(signUpUseCase,firestore, firebaseAuth, dispatcher.testDispatcher)
    }

    @Test
    fun `when email and password and username are empty, should return failure state`() = runTest {
        //Given
        val email = ""
        val username = ""
        val password = ""

        //When
        signUpViewModel.signUp(email,password, username)

        //Then
        signUpViewModel.signUpState.test {
            assertTrue(awaitItem() is UIState.OnFailure)
        }
    }

    @Test
    fun `when email is empty and password ann username are not empty, should return failure state`() = runTest {
        //Given
        val email = ""
        val username = "username"
        val password = "password"

        //When
        signUpViewModel.signUp(email,password, username)

        //Then
        signUpViewModel.signUpState.test {
            assertTrue(awaitItem() is UIState.OnFailure)
        }
    }

    @Test
    fun `when email is empty and password are empty and username is not empty, should return failure state`() = runTest {
        //Given
        val email = ""
        val username = ""
        val password = "password"

        //When
        signUpViewModel.signUp(email,password, username)

        //Then
        signUpViewModel.signUpState.test {
            assertTrue(awaitItem() is UIState.OnFailure)
        }
    }

}