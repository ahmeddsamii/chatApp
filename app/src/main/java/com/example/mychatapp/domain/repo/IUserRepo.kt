package com.example.mychatapp.domain.repo


import com.google.firebase.firestore.FirebaseFirestore

interface IUserRepo {
    fun getAllUsers(): FirebaseFirestore
    fun getLoggedUserData():FirebaseFirestore
}