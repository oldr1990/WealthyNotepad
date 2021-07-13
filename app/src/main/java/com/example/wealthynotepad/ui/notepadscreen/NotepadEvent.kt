package com.example.wealthynotepad.ui.notepadscreen

import com.example.wealthynotepad.data.Notes

sealed class NotepadEvent {
    class Success(val notes: List<Notes>) : NotepadEvent()
    class SuccessAddDelete(val message: String) : NotepadEvent()
    class Failure(val message: String) : NotepadEvent()
    object Logout: NotepadEvent()
    object Loading : NotepadEvent()
    object Empty : NotepadEvent()
}
