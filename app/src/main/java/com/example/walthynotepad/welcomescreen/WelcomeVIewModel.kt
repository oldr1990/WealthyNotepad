package com.example.walthynotepad.welcomescreen

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walthynotepad.data.Constants
import com.example.walthynotepad.data.UserEntries
import com.example.walthynotepad.repository.FirebaseRepository
import com.example.walthynotepad.util.DispatcherProvider
import com.example.walthynotepad.util.LoginEvent
import com.example.walthynotepad.util.LoginResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import okhttp3.internal.wait

class WelcomeVIewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    init {
        viewModelScope.launch(dispatcher.io) {
            if (firebaseRepository.checkLoginData()) {
                val data = firebaseRepository.getLoginData()
                if (data.password != Constants.emptyString && data.email != Constants.emptyString)
                    login(data)
            }
            firebaseRepository.authCallBack.collect {
                when (it) {
                    is LoginResource.Success -> {
                        _loginFlow.value = LoginEvent.Success(it.data)
                    }
                    is LoginResource.Empty -> {
                        _loginFlow.value = LoginEvent.Empty
                    }
                    is LoginResource.Error -> {
                        _loginFlow.value = LoginEvent.Failure(it.data)
                    }
                }
            }
            firebaseRepository.getUserUID().let {
                if (it != null) LoginEvent.Success(it)
            }

        }


    }


    private val _loginFlow = MutableStateFlow<LoginEvent>(LoginEvent.Empty)
    val loginFlow: StateFlow<LoginEvent> = _loginFlow

    fun register(userdata: UserEntries) {
        viewModelScope.launch(dispatcher.io) {
            _loginFlow.value = LoginEvent.Loading
            firebaseRepository.setLoginData(userdata)
            firebaseRepository.registerUser(userdata)
        }
    }

    fun login(userdata: UserEntries) {
        viewModelScope.launch(dispatcher.io) {
            _loginFlow.value = LoginEvent.Loading
            firebaseRepository.setLoginData(userdata)
            firebaseRepository.loginUser(userdata)
        }
    }
}