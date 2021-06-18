package com.example.walthynotepad.notepadscreen


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
):ViewModel() {
   private var uid: String = String()

    private val _noteCallBack =  MutableStateFlow<NotepadEvent>(NotepadEvent.Empty)
    val noteCallBack : StateFlow<NotepadEvent> = _noteCallBack

    init {
        viewModelScope.launch(dispatcher.io)  {
            if (firebaseRepository.checkLoginState()) {
                uid = firebaseRepository.getUserUID().toString()
                getNotes()
                _noteCallBack.value = NotepadEvent.Loading
                firebaseRepository.notepadCallBack.collect {
                    when (it) {
                        is NotesResource.Success -> {
                            _noteCallBack.value = NotepadEvent.Success(it.data?.sortedByDescending { it.date } ?: listOf(Notes()))
                        }
                        is NotesResource.SuccessAdd -> {
                            _noteCallBack.value = NotepadEvent.SuccessAddDelete(Constants.addedLabel)
                        }
                        is NotesResource.SuccessDelete -> {
                            _noteCallBack.value = NotepadEvent.SuccessAddDelete(Constants.deletedLabel)
                        }
                        is NotesResource.Error -> {
                            _noteCallBack.value = NotepadEvent.Failure(it.toString())
                        }
                        is NotesResource.Empty -> {
                        }
                    }
                }
            }

        }
    }

    fun addNote(note: Notes){
        viewModelScope.launch(dispatcher.io) {
            firebaseRepository.addNote(note)
        }
    }

    fun deleteNote(note: Notes){
        viewModelScope.launch(dispatcher.io)  {
            firebaseRepository.deleteNote(note)
        }
    }

    fun getNotes(){
        viewModelScope.launch(dispatcher.io)  {
            firebaseRepository.getNotes(uid)
        }
    }
}