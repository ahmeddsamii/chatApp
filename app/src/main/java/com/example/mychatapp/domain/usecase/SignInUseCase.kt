package com.example.mychatapp.domain.usecase

import com.example.mychatapp.domain.repo.IAuthRepo
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepo: IAuthRepo,
) {
    suspend operator fun invoke(email:String, password:String) = authRepo.signIn(email, password)
}
