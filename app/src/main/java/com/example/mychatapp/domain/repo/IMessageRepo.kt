package com.example.mychatapp.domain.repo

import com.example.mychatapp.domain.entity.Message
import kotlinx.coroutines.flow.Flow

interface IMessageRepo {
    suspend fun getMessages(friendUid:String): Flow<List<Message>>
}