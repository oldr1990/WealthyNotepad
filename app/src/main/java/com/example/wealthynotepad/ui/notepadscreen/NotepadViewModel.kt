package com.example.wealthynotepad.ui.notepadscreen

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.wealthynotepad.data.Constants
import com.example.wealthynotepad.data.Constants.ERROR_DATE
import com.example.wealthynotepad.data.Notes
import com.example.wealthynotepad.repository.FirebaseRepository
import com.example.wealthynotepad.util.DispatcherProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotepadViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    sealed class NotepadEvent {
        class Success(val notes: List<Notes>) : NotepadEvent()
        class SuccessAddDelete(val message: String) : NotepadEvent()
        class Failure(val message: String) : NotepadEvent()
        object Logout: NotepadEvent()
        object Loading : NotepadEvent()
        object Empty : NotepadEvent()
    }


    private var uid: String = String()

    var isHandled = false

    private val _listOfNotes = MutableStateFlow(listOf(Notes()))
    val listOfNotes: StateFlow<List<Notes>> = _listOfNotes

    private val _noteCallBack = MutableStateFlow<NotepadEvent>(NotepadEvent.Empty)
    val noteCallBack: StateFlow<NotepadEvent> = _noteCallBack

    init {
        viewModelScope.launch(dispatcher.io) {
            if (firebaseRepository.checkLoginState()) {
                uid = firebaseRepository.getUserUID().toString()
                getNotes()
                _noteCallBack.value = NotepadEvent.Loading
                firebaseRepository.notepadCallBack.collect { response ->
                    isHandled = false
                    when (response) {
                        is NotesResource.Success -> {
                            if (response.data != null) {
                                val list = response.data.sortedByDescending{ it.date }
                                _listOfNotes.value = list
                                _noteCallBack.value = NotepadEvent.Success(list)
                            }
                        }
                        is NotesResource.SuccessAdd -> {
                            _noteCallBack.value =
                                NotepadEvent.SuccessAddDelete(Constants.NOTE_ADDED_LABEL)
                        }
                        is NotesResource.SuccessDelete -> {
                            _noteCallBack.value =
                                NotepadEvent.SuccessAddDelete(Constants.NOTE_DELETED_LABEL)
                        }
                        is NotesResource.Error -> {
                            isHandled = false
                            _noteCallBack.value = NotepadEvent.Failure(response.message.toString())
                        }
                        is NotesResource.Empty -> {}
                        is NotesResource.Logout -> {
                            _noteCallBack.value = NotepadEvent.Logout
                        }
                    }
                }
            }
            else _noteCallBack.value = NotepadEvent.Logout
        }
    }

    fun addNote(note: Notes) {
        if (note.date.toLongOrNull()!=null){
            viewModelScope.launch(dispatcher.io) {
                _noteCallBack.value = NotepadEvent.Loading
                firebaseRepository.addNote(note)
            }
        }
        else _noteCallBack.value = NotepadEvent.Failure(ERROR_DATE)
    }

    fun deleteNote(note: Notes) {
        viewModelScope.launch(dispatcher.io) {
            _noteCallBack.value = NotepadEvent.Loading
            firebaseRepository.deleteNote(note)
        }
    }

    fun logout(){
        viewModelScope.launch(dispatcher.io) {
            _noteCallBack.value = NotepadEvent.Loading
              firebaseRepository.logout()
        }
    }

     private fun getNotes() {
        viewModelScope.launch(dispatcher.io) {
            _noteCallBack.value = NotepadEvent.Loading
            firebaseRepository.getNotes(uid)
        }
    }
}