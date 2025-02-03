package com.example.mychatapp.presentation.ui.home

import androidx.recyclerview.widget.DiffUtil
import com.example.mychatapp.domain.entity.User

class UsersDiffUtil: DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}