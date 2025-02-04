package com.example.mychatapp.presentation.ui.signin

import app.cash.turbine.test
import com.example.mychatapp.domain.usecase.SignInUseCase
import com.example.mychatapp.utils.UIState
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.rules.TestWatcher
import org.junit.runner.Description

class SignInViewModelTest{
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var signInUseCase: SignInUseCase
    private lateinit var dispatcher: MainTestDispatcher

    @Before
    fun setup(){
        signInUseCase = mockk(relaxed = true)
        dispatcher = MainTestDispatcher()
        signInViewModel = SignInViewModel(signInUseCase, dispatcher.testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }


    @Test
    fun `when email and password are empty, should return failure state`()= runTest {

        // Given
        val email = ""
        val password = ""

        // When
        signInViewModel.signIn(email, password)

        // Then
        signInViewModel.signInState.test {
            assertTrue(awaitItem() is UIState.OnFailure)
        }
    }

    @Test
    fun `when sign in is successful, should return success state`() = runTest {
        // Given
        val email = "test@gmail.com"
        val password = "123456789"

        val mockTask: Task<AuthResult> = mockk(relaxed = true)

        // Mock the signInUseCase to invoke the listener
        coEvery { signInUseCase.invoke(email, password).addOnCompleteListener(any()) } coAnswers {
            val listener = it.invocation.args[0] as OnCompleteListener<AuthResult>
            listener.onComplete(mockTask)
            mockTask
        }
        coEvery { mockTask.isSuccessful } coAnswers { true }

        // When
        signInViewModel.signIn(email, password)

        // Make sure the dispatcher completes all tasks
        dispatcher.testDispatcher.scheduler.advanceUntilIdle()

        // Then
        signInViewModel.signInState.test {
            assertTrue(awaitItem() is UIState.OnSuccess<*>)
        }
    }

    @Test
    fun `when invalid credential entered, should return failure state`() = runTest {
        // Given
        val email = "ahmed@gmail.com"
        val password = "12345"

        val mockTask: Task<AuthResult> = mockk()
        val mockException: FirebaseAuthInvalidCredentialsException = mockk()

        coEvery { signInUseCase.invoke(email, password).addOnFailureListener(any()) } coAnswers {
            val listener = it.invocation.args as OnFailureListener
            listener.onFailure(mockException)
            mockTask
        }

        // When
        try {
            signInViewModel.signIn(email, password)
            dispatcher.testDispatcher.scheduler.advanceUntilIdle()
        }catch (ex:Exception){
            assertTrue(ex is FirebaseAuthInvalidCredentialsException)
            assert(signInViewModel.signInState.value is UIState.OnFailure){"Current signInState: ${signInViewModel.signInState.value}"}
        }
    }



}


@OptIn(ExperimentalCoroutinesApi::class)
class MainTestDispatcher(
val testDispatcher: TestDispatcher = StandardTestDispatcher()
): TestWatcher(){
    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }
}