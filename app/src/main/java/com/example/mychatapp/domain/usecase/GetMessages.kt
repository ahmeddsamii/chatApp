package com.example.mychatapp.domain.usecase

import com.example.mychatapp.domain.repo.IMessageRepo

class GetMessages(private val messageRepo:IMessageRepo) {
    suspend operator fun invoke(friendUid:String) = messageRepo.getMessages(friendUid)
}