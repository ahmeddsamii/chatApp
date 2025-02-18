package com.example.mychatapp.domain.entity

data class Message(
    val message: String = "",
    val receiverUid: String = "",
    val senderUid: String = "",
    val time: String = ""
)
