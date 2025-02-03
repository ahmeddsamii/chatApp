package com.example.mychatapp.domain.repo

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

interface IAuthRepo {
    suspend fun signIn(email:String, password:String): Task<AuthResult>
    suspend fun signUp(email: String, password: String, username:String): Task<AuthResult>
}