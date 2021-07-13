package com.example.wealthynotepad.ui.welcomescreen

sealed class LoginEvent {
    class Success(val uid: String) : LoginEvent()
    class Failure(val errorText: String) : LoginEvent()
    object Loading : LoginEvent()
    object Empty : LoginEvent()
}
