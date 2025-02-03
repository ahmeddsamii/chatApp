package com.example.mychatapp.data.repo_impl

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mychatapp.domain.entity.User
import com.example.mychatapp.domain.repo.IUserRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class UserRepoImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore,
) : IUserRepo {
    override fun getAllUsers(): FirebaseFirestore {
       return firebaseFirestore
    }

    override fun getLoggedUserData(): FirebaseFirestore {
        return firebaseFirestore
    }
}