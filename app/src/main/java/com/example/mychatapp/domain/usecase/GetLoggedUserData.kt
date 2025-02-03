package com.example.mychatapp.domain.usecase

import com.example.mychatapp.domain.repo.IUserRepo
import javax.inject.Inject

class GetLoggedUserData @Inject constructor(
    private val userRepo: IUserRepo
) {
    operator fun invoke() = userRepo.getLoggedUserData()
}