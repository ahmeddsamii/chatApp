package com.example.mychatapp.utils

sealed class UIState {
    data object OnIdle: UIState()
    data object OnLoading: UIState()
    class OnFailure(val errorMessage:String): UIState()
    class OnSuccess<out T>(val data: T?): UIState()
}