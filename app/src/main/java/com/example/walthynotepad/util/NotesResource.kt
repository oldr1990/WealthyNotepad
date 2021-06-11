package com.example.walthynotepad.util

import com.example.walthynotepad.data.Notes

sealed class NotesResource<T>(val data: List<Notes>?, message: String?) {
        class Success<T>(data: List<Notes>) : NotesResource<T>(data, null)
        class SuccessAdd<T>() : NotesResource<T>(null, null )
        class SuccessDelete<T>() : NotesResource<T>(null, null )
        class Error<T>(message: String) : NotesResource<T>(null, message)
        class Empty<T>() : NotesResource<T>(null, null )
    }