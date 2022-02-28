package com.example.wealthynotepad.ui.welcomescreen


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthynotepad.data.UserEntries
import com.example.wealthynotepad.repository.FirebaseRepository
import com.example.wealthynotepad.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor(
    private val dispatcher: DispatcherProvider,
    private val firebaseRepository: FirebaseRepository
) : ViewModel() {

    private val _loginFlow = mutableStateOf(WelcomeScreenState())
    val loginFlow: State<WelcomeScreenState> = _loginFlow

    private val _snackdbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackdbarEvent.asSharedFlow()

     init {
        if (firebaseRepository.checkLoginData()) {
            val data = firebaseRepository.getLoginData()
            if (data.password.isNotEmpty() && data.email.isNotEmpty()) {
                login(data)
            }
        }
    }

    fun register(userdata: UserEntries) {
        if (inputDataVerification(userdata)) {
            return
        }
        viewModelScope.launch(dispatcher.io) {
            loadingState()
            firebaseRepository.registerUser(userdata).collectLatest {
                responseHandler(it, userdata)
            }
        }
    }

    private fun responseHandler(response: NetworkResponse<String>, userdata: UserEntries) {
        when (response) {
            is NetworkResponse.Error -> {
                viewModelScope.launch {
                    _snackdbarEvent.emit(response.data)
                    loadingState(false)
                }
            }
            is NetworkResponse.Success -> {
                firebaseRepository.setLoginData(userdata)
                _loginFlow.value =
                    loginFlow.value.copy(isLoading = false, userUID = response.data)
            }
        }

    }

    fun login(userdata: UserEntries) {
        if (inputDataVerification(userdata)) {
            return
        }
        viewModelScope.launch(dispatcher.unconfined) {
            loadingState()
            firebaseRepository.loginUser(userdata).collectLatest {
                responseHandler(it, userdata)
            }
        }
    }

    private fun inputDataVerification(userdata: UserEntries): Boolean {
        _loginFlow.value = loginFlow.value.copy(
            isLoading = false,
            emailError = userdata.email.isNotEmail(),
            passwordError = userdata.password.isNotPassword()
        )
        return loginFlow.value.passwordError || loginFlow.value.emailError
    }

    private fun String.isNotEmail(): Boolean =
        !(this.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches())

    private fun String.isNotPassword(): Boolean = !Pattern
        .compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$")
        .matcher(this).matches()

    private fun loadingState(state: Boolean = true) {
        _loginFlow.value = loginFlow.value
            .copy(isLoading = state, emailError = false, passwordError = false)
    }
}

data class WelcomeScreenState(
    val isLoading: Boolean = false,
    val userUID: String = "",
    val emailError: Boolean = false,
    val passwordError: Boolean = false
)