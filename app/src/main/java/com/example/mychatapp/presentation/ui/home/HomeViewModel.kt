package com.example.mychatapp.presentation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mychatapp.domain.entity.User
import com.example.mychatapp.domain.usecase.GetAllUsersUseCase
import com.example.mychatapp.domain.usecase.GetLoggedUserData
import com.example.mychatapp.utils.UIState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getAllUsersUseCase: GetAllUsersUseCase,
    private val firebaseAuth: FirebaseAuth,
    private val getLoggedUserData: GetLoggedUserData
): ViewModel() {

    private val _allUsers = MutableStateFlow<UIState>(UIState.OnIdle)
    val allUsers = _allUsers.asStateFlow()

    private val _currentUser = MutableLiveData<User?>()
    val currentUser:LiveData<User?> = _currentUser

    fun getAllUsers(){
        _allUsers.value = UIState.OnLoading

        getAllUsersUseCase.invoke().collection("Users")
            .get()
            .addOnSuccessListener { snapshot ->
                val users = mutableListOf<User>()
                snapshot.documents.forEach { document ->
                    val user = document.toObject(User::class.java)
                    if (user != null && user.userId != firebaseAuth.currentUser?.uid) {
                        users.add(user)
                    }
                }
                Log.d("TAG", "Users fetched: ${users.size}")
                _allUsers.value = UIState.OnSuccess(users)
            }.addOnFailureListener { ex->
                _allUsers.value = UIState.OnFailure(ex.message!!)
            }
    }

    fun getDataOfLoggedUser(){
        getLoggedUserData.invoke().collection("Users").get().addOnSuccessListener { snapshot->
            snapshot.documents.forEach { document->
                val user = document.toObject(User::class.java)
                if (user!!.userId == firebaseAuth.currentUser!!.uid){
                    _currentUser.value = user
                }
            }
        }
    }

}