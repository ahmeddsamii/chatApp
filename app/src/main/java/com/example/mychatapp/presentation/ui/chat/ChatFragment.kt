package com.example.mychatapp.presentation.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mychatapp.databinding.FragmentChatBinding
import com.example.mychatapp.domain.entity.Message
import com.example.mychatapp.domain.entity.User
import com.example.mychatapp.utils.UIState
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ChatFragment : Fragment() {

    private lateinit var args: ChatFragmentArgs
    private lateinit var binding:FragmentChatBinding
    private lateinit var user:User
    private val chatViewMode:ChatViewModel by viewModels()
    private val firebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        args = ChatFragmentArgs.fromBundle(requireArguments())
        user = args.user
        setUpOpponentPersonData()
        user.userId?.let { chatViewMode.getMessage(it) }
        observeLoadingMessagesState()
        binding.sendBtn.setOnClickListener {
            sendMessage()
        }

    }

    private fun observeLoadingMessagesState(){
        lifecycleScope.launch {
            chatViewMode.messages.collect{
                when(it){
                    is UIState.OnFailure -> Log.e("TAG", "onViewCreated: ${it.errorMessage}", )
                    UIState.OnIdle -> {}
                    UIState.OnLoading -> {}
                    is UIState.OnSuccess<*> ->{
                        val data = it.data as List<Message>
                    }
                }
            }
        }
    }

    private fun setUpOpponentPersonData(){
        binding.chatUserName.text = user.username
        binding.chatUserStatus.text = user.status
        Glide.with(requireContext()).load(user.imageUrl).into(binding.chatImageViewUser)
    }

    private fun sendMessage(){
        chatViewMode.sendMessage(
            senderUid = firebaseAuth.currentUser!!.uid,
            receiverUid = user.userId!!,
            message = binding.editTextMessage.getText().toString(),
            friendName = user.username!!,
            friendImage = user.imageUrl!!,
        )
        clearTheTextField()
        observeSendingMessageState()
    }

    private fun clearTheTextField(){
        binding.editTextMessage.text.clear()
    }

    private fun observeSendingMessageState(){
        lifecycleScope.launch {
            chatViewMode.conversationState.collect{state->
                when(state){
                    is UIState.OnFailure -> {
                        Toast.makeText(requireContext(), "Something went wrong!", Toast.LENGTH_SHORT).show()}
                    UIState.OnIdle -> {}
                    UIState.OnLoading -> {
                        Toast.makeText(requireContext(), "Loading", Toast.LENGTH_SHORT).show()}
                    is UIState.OnSuccess<*> -> {
                        Toast.makeText(requireContext(), "Message sent!", Toast.LENGTH_SHORT).show()}
                }
                }
            }
        }

    }
