package com.example.mychatapp.presentation.ui.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.mychatapp.R
import com.example.mychatapp.databinding.UserListItemBinding
import com.example.mychatapp.domain.entity.User

class UsersAdapter(private val listener:OnUserClickListener): ListAdapter<User, UsersAdapter.UserViewHolder>(UsersDiffUtil()) {

    class UserViewHolder(val binding: UserListItemBinding): ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val inflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = UserListItemBinding.inflate(inflater, parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = getItem(position)
        with(holder.binding){

            // username of the user
            userName.text =
                currentUser.username?.split("\\s".toRegex().toString())?.get(0) ?: ""

            // the status of the user
            if (currentUser.status?.equals("Online") == true){
                statusOnline.setImageResource(R.drawable.online_status)
            }else{
                statusOnline.setImageResource(R.drawable.offline_status)
            }

            // the profile picture of the user
            Glide.with(holder.itemView.context).load(currentUser.imageUrl).into(imageViewUser)


            holder.itemView.setOnClickListener{
                listener.onUserSelect(position, currentUser)
            }
        }

    }
}