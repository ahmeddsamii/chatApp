package com.example.mychatapp.data.repo_impl

import com.example.mychatapp.domain.entity.Message
import com.example.mychatapp.domain.repo.IMessageRepo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject


class MessageRepoImpl @Inject constructor(private val firestore: FirebaseFirestore) : IMessageRepo {
    override suspend fun getMessages(friendUid: String): Flow<List<Message>> = callbackFlow {
        val currentUid = FirebaseAuth.getInstance().currentUser!!.uid
        val chatId = "$currentUid$friendUid"

        val listOfMessages = mutableListOf<Message>()

        val listener = firestore.collection("Messages")
            .document(chatId)
            .collection("Chats")
            .addSnapshotListener { value, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                if (value != null && !value.isEmpty) {
                    listOfMessages.clear()

                    value.documents.forEach { document ->
                        val message = document.toObject(Message::class.java)
                        if (message != null && (
                                    (message.senderUid == currentUid && message.receiverUid == friendUid) ||
                                            (message.receiverUid == currentUid && message.senderUid == friendUid)
                                    )) {
                            listOfMessages.add(message)
                        }
                    }

                    trySend(listOfMessages.toList())
                }
            }

        // Clean up the listener when the flow is cancelled
        awaitClose {
            listener.remove()
        }
    }
}