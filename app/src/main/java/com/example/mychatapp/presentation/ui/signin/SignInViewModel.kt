package com.example.mychatapp.presentation.ui.signin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.domain.usecase.SignInUseCase
import com.example.mychatapp.utils.UIState
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _signInState = MutableStateFlow<UIState>(UIState.OnIdle)
    val signInState = _signInState.asStateFlow()

    private fun checkEmailAndPasswordAndUsername(email: String, password: String): Boolean {
        return email.isNotEmpty() || password.isNotEmpty()
    }


    fun signIn(email: String, password: String) {

        _signInState.value = UIState.OnLoading

        if (!checkEmailAndPasswordAndUsername(email, password)){
            _signInState.value = UIState.OnFailure("Email and Password must be filled!")
        }else{
            viewModelScope.launch {
                signInUseCase.invoke(email, password).addOnCompleteListener{
                    _signInState.value = UIState.OnSuccess(null)
                }.addOnFailureListener{exception ->
                    when(exception){
                        is FirebaseAuthInvalidCredentialsException -> _signInState.value = UIState.OnFailure("Invalid Email or Password")
                        else -> UIState.OnFailure("Something went wrong")
                    }
                }
            }

        }
    }
}