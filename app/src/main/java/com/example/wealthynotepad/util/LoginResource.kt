package com.example.wealthynotepad.util

sealed class LoginResource<T>(val data: String) {
    class Success<T>(data: String) : LoginResource<T>(data)
    class Error<T>(data: String) : LoginResource<T>(data)
    class Empty<T>() : LoginResource<T>("")
}