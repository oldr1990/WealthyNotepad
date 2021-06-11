package com.example.walthynotepad.util

import com.example.walthynotepad.data.Notes

sealed class NotepadEvent {
    class Success(val notes: List<Notes>) : NotepadEvent()
    class SuccessAddDelete(val message: String) : NotepadEvent()
    class Failure(val message: String) : NotepadEvent()
    object Loading : NotepadEvent()
    object Empty : NotepadEvent()
}
