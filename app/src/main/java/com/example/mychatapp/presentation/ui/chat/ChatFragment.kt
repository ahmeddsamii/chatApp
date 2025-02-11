package com.example.mychatapp.presentation.ui.chat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.mychatapp.databinding.FragmentChatBinding
import com.example.mychatapp.domain.entity.User
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint

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


        binding.sendBtn.setOnClickListener {
            sendMessage()
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
    }

    private fun clearTheTextField(){
        binding.editTextMessage.text.clear()
    }

}