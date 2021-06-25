package com.example.walthynotepad.ui.notepadscreen

import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walthynotepad.data.Constants
import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.repository.FirebaseRepository
import com.example.walthynotepad.util.DispatcherProvider
import com.example.walthynotepad.util.NotepadEvent
import com.example.walthynotepad.util.NotesResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotepadViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val dispatcher: DispatcherProvider
) : ViewModel() {

    private var uid: String = String()

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
                    when (response) {
                        is NotesResource.Success -> {
                            if (response.data != null) {
                                val list = response.data.sortedBy{ it.date.reversed() }
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
                            _noteCallBack.value = NotepadEvent.Failure(response.toString())
                            _noteCallBack.value = NotepadEvent.Empty
                        }
                        is NotesResource.Empty -> {}
                        is NotesResource.Logout -> {
                            _noteCallBack.value = NotepadEvent.Logout
                        }
                    }
                }
            }
        }
    }

    fun addNote(note: Notes) {
        viewModelScope.launch(dispatcher.io) {
            _noteCallBack.value = NotepadEvent.Loading
            firebaseRepository.addNote(note)
        }
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
            firebaseRepository.getNotes(uid)
        }
    }
}