package com.example.wealthynotepad.ui.notepadscreen


import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthynotepad.data.Constants.ERROR_DATE
import com.example.wealthynotepad.data.Notes
import com.example.wealthynotepad.repository.FirebaseRepository
import com.example.wealthynotepad.ui.welcomescreen.NetworkResponse
import com.example.wealthynotepad.util.DispatcherProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotepadViewModel @Inject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {
    private val _state = mutableStateOf(NotepadScreenState())
    val state: State<NotepadScreenState> = _state

    private val _snackdbarEvent = MutableSharedFlow<String>()
    val snackbarEvent = _snackdbarEvent.asSharedFlow()

    init {
        viewModelScope.launch(dispatcher.main) {
            if (firebaseRepository.checkLoginState()) {
                    loadingState()
                    firebaseRepository.getNotes{
                        when(it){
                            is NetworkResponse.Error -> messageState("Cant download notes")
                            is NetworkResponse.Success -> _state.value = state.value.copy(
                                isLoading = false,
                                notes = it.data.sortedByDescending { item -> item.date.toBigDecimal() })
                        }
                    }
                loadingState()
            }
            else {
                logoutState()
            }
        }
    }

    fun addNote(note: Notes) {
        if (note.date.toLongOrNull()!=null){
            viewModelScope.launch(dispatcher.io) {
                loadingState()
                firebaseRepository.addNote(note).collectLatest {
                    messageState(it.data)
                }
            }
        }
        else viewModelScope.launch {
            messageState(ERROR_DATE)
        }
    }

    fun deleteNote(note: Notes) {
        viewModelScope.launch(dispatcher.io) {
            loadingState()
            firebaseRepository.deleteNote(note).collectLatest {
                messageState(it.data)
            }
        }
    }

    fun logout(){
        viewModelScope.launch(dispatcher.io) {
            loadingState()
              firebaseRepository.logout().collectLatest {
                  when(it){
                      is NetworkResponse.Error -> messageState("Unexpected Error")
                      is NetworkResponse.Success -> logoutState()
                  }
              }
        }
    }
    private fun logoutState(){
        _state.value = state.value.copy(
            isLoading = false,
            loggedOut = true
        )
    }

    private fun messageState(msg: String){
        viewModelScope.launch{
            _snackdbarEvent.emit(msg)
        }
    }

    private fun loadingState(){
        _state.value = state.value.copy( isLoading = true)
    }

}

data class NotepadScreenState(
    val isLoading: Boolean = false,
    val loggedOut: Boolean = false,
    val notes: List<Notes> = emptyList()
)