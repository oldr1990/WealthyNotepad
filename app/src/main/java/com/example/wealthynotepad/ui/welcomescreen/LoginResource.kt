package com.example.wealthynotepad.ui.welcomescreen

sealed class LoginResource<T>(val data: String) {
    class Success<T>(data: String) : LoginResource<T>(data)
    class Error<T>(data: String) : LoginResource<T>(data)
    class Empty<T>() : LoginResource<T>("")
}

sealed class NetworkResponse<T>(val data: T) {
    class Success<T>(data: T) : NetworkResponse<T>(data)
    class Error<T>(data: T) : NetworkResponse<T>(data)
}