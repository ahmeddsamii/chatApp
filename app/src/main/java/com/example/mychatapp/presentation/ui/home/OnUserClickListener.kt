package com.example.mychatapp.presentation.ui.home

import com.example.mychatapp.domain.entity.User

interface OnUserClickListener {
    fun onUserSelect(position:Int, user: User)
}