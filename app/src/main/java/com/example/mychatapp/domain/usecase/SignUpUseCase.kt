package com.example.mychatapp.domain.usecase

import android.provider.ContactsContract.CommonDataKinds.Email
import com.example.mychatapp.domain.repo.IAuthRepo
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepo: IAuthRepo
) {
    suspend operator fun invoke(email: String, password:String, username:String) = authRepo.signUp(email, password, username)
}