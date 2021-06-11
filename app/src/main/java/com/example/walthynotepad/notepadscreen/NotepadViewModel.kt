package com.example.walthynotepad.notepadscreen

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.repository.FirebaseRepository
import com.example.walthynotepad.util.DispatcherProvider
import com.example.walthynotepad.util.NotesResource
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotepadViewModel @ViewModelInject constructor(
    private val firebaseRepository: FirebaseRepository,
    private val dispatcher: DispatcherProvider
):ViewModel() {
   private var uid: String = String()

    init {
        viewModelScope.launch(dispatcher.io)  {
            uid = firebaseRepository.getUserUID()?: ""
            firebaseRepository.notepadCallBack.collect {
                when(it){
                    is NotesResource.Success -> {}
                    is NotesResource.SuccessAdd -> {}
                    is NotesResource.SuccessDelete -> {}
                    is NotesResource.Error -> {}
                    is NotesResource.Empty -> {}
                }
            }
        }
    }
    sealed class NotepadEvent {
        class Success(val notes: List<Notes>) : NotepadEvent()
        class SuccessAddDelete(val message: String) : NotepadEvent()
        class Failure(val message: String) : NotepadEvent()
        object Loading : NotepadEvent()
        object Empty : NotepadEvent()
    }

    fun addNote(note: Notes){
        viewModelScope.launch(dispatcher.io) {
            firebaseRepository.addNote(note)
        }
    }

    fun deleteNote( id: String){
        viewModelScope.launch(dispatcher.io)  {
            firebaseRepository.deleteNote(uid, id)
        }
    }

    fun getNotes(){
        viewModelScope.launch(dispatcher.io)  {
            firebaseRepository.getNotes()
        }
    }
}