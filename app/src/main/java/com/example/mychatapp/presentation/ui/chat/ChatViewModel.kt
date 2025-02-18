package com.example.mychatapp.presentation.ui.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychatapp.data.repo_impl.MessageRepoImpl
import com.example.mychatapp.domain.usecase.GetMessages
import com.example.mychatapp.utils.UIState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth,
    private val messageRepo: MessageRepoImpl,
    private val getMessageUseCase: GetMessages
) : ViewModel() {

    private val _conversationState = MutableStateFlow<UIState>(UIState.OnIdle)
    val conversationState = _conversationState.asStateFlow()

    private val _messages = MutableStateFlow<UIState>(UIState.OnIdle)
    val messages = _messages.asStateFlow()

    fun sendMessage(
        senderUid: String,
        receiverUid: String,
        message: String,
        friendName: String,
        friendImage: String,
    ) {
        viewModelScope.launch(Dispatchers.IO) {

            _conversationState.value = UIState.OnLoading

            addMessageToFirebase(
                senderUid = senderUid,
                receiverUid = receiverUid,
                message = message
            )

            addConversationToFirebase(
                senderUid = senderUid, receiverUid = receiverUid, message = message,
                friendName = friendName, friendImage = friendImage
            )

            updateReceiverConversation(
                receiverUid = receiverUid,
                message = message,
                friendName = friendName
            )
        }
    }

    private fun getTime(): String {
        val formatter = SimpleDateFormat("HH:mm:ss")
        val date = Date(System.currentTimeMillis())
        return formatter.format(date)
    }

    private fun addMessageToFirebase(senderUid: String, receiverUid: String, message: String) {
        val hashMap = hashMapOf<String, Any>(
            "senderUid" to senderUid,
            "receiverUid" to receiverUid,
            "message" to message,
            "time" to getTime()
        )

        val uniqueId = listOf(senderUid, receiverUid).sorted().joinToString(separator = "")

        // Add messages to Firestore with success/failure listeners
        firestore.collection("Messages")
            .document(uniqueId)
            .collection("Chats")
            .document(getTime())
            .set(hashMap)
            .addOnSuccessListener {
                _conversationState.value = UIState.OnSuccess(it)
                Log.d("ChatViewModel", "Message sent!")
            }
            .addOnFailureListener { e ->
                _conversationState.value = UIState.OnFailure(e.message ?: "Something went wrong")
                Log.e("ChatViewModel", "Error sending message", e)
            }
    }

    private fun addConversationToFirebase(
        senderUid: String,
        receiverUid: String,
        message: String,
        friendName: String,
        friendImage: String,
    ) {
        val hashMapOfRecentChats = hashMapOf<String, Any>(
            "receiverUid" to receiverUid,
            "time" to getTime(),
            "senderUid" to senderUid,
            "message" to message,
            "friendImage" to friendImage,
            "friendName" to friendName,
            "sender" to "you"
        )

        // Add conversation to Firestore with success/failure listeners
        firestore.collection("Conversation${firebaseAuth.currentUser!!.uid}")
            .document("receiver")
            .set(hashMapOfRecentChats)
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Conversation saved successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error saving conversation", e)
            }
    }


    private fun updateReceiverConversation(
        receiverUid: String,
        message: String,
        friendName: String,
    ) {
        // Update receiver's conversation
        firestore.collection("Conversation${receiverUid}")
            .document(firebaseAuth.currentUser!!.uid)
            .update(
                mapOf(
                    "message" to message,
                    "time" to getTime(),
                    "friendName" to friendName
                )
            )
            .addOnSuccessListener {
                Log.d("ChatViewModel", "Receiver conversation updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("ChatViewModel", "Error updating receiver conversation", e)
            }
    }

    fun getMessage(friendUid: String) {
        viewModelScope.launch {
            _messages.value = UIState.OnLoading
            getMessageUseCase.invoke(friendUid)
                .catch { _messages.value = UIState.OnFailure(it.message ?: "Something went wrong") }
                .collect { _messages.value = UIState.OnSuccess(it) }
        }
    }

}