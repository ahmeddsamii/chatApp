package com.example.mychatapp.presentation.ui.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.domain.usecase.SignUpUseCase
import com.example.mychatapp.presentation.di.IoDispatcher
import com.example.mychatapp.utils.UIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase,
    private val firestore: FirebaseFirestore,
    private val auth:FirebaseAuth,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
):ViewModel() {
    private val _signUpState = MutableStateFlow<UIState>(UIState.OnIdle)
    val signUpState = _signUpState.asStateFlow()


    private fun checkEmailAndPasswordAndUsername(email: String, password: String, username:String): Boolean {
        return email.isEmpty() || password.isEmpty() || username.isEmpty()
    }

    fun signUp(email: String, password: String, username: String) {
        if (checkEmailAndPasswordAndUsername(email, password, username)) {
            _signUpState.value = UIState.OnFailure("Email, Password and Username cannot be empty!")
        }else{
            _signUpState.value = UIState.OnLoading

            viewModelScope.launch(dispatcher) {
                // First check if username exists
                firestore.collection("Users")
                    .whereEqualTo("userEmail", email)
                    .get()
                    .addOnSuccessListener { documents ->
                        if (documents.isEmpty) {
                            // Username is not taken, proceed with email check and registration
                            createNewUser(email, password, username)
                        } else {
                            _signUpState.value = UIState.OnFailure("Email already exists!")
                        }
                    }
                    .addOnFailureListener {
                        _signUpState.value = UIState.OnFailure("Error checking existing user: ${it.message}")
                    }
            }
        }
    }


    private fun createNewUser(email: String, password: String, username: String) {
        viewModelScope.launch {
            signUpUseCase.invoke(email, password, username)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser


                        firestore.collection("Users").document(user!!.uid)
                            .set(createHashMap(username))
                            .addOnSuccessListener {
                                _signUpState.value = UIState.OnSuccess(user)
                            }
                            .addOnFailureListener { e ->
                                _signUpState.value = UIState.OnFailure("Failed to save user data: ${e.message}")
                            }
                    } else {
                        _signUpState.value = UIState.OnFailure(task.exception?.message ?: "Registration failed")
                    }
                }
        }
    }

    private fun createHashMap(username:String):HashMap<String, String?>{
        val hashMap = hashMapOf(
            "userId" to auth.currentUser!!.uid,
            "username" to username,
            "userEmail" to auth.currentUser!!.email,
            "status" to "default",
            "imageUrl" to "https://www.pngarts.com/files/6/User-Avatar-in-Suit-PNG.png"
        )
        return hashMap
    }
}