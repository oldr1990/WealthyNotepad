package com.example.wealthynotepad.util

import com.example.wealthynotepad.data.Notes

sealed class NotesResource<T>(val data: List<Notes>?,val  message: String?) {
        class Success<T>(data: List<Notes>) : NotesResource<T>(data, null)
        class SuccessAdd<T>() : NotesResource<T>(null, null )
        class SuccessDelete<T>() : NotesResource<T>(null, null )
        class Logout<T>(): NotesResource<T>(null,null)
        class Error<T>(message: String) : NotesResource<T>(null, message)
        class Empty<T>() : NotesResource<T>(null, null )
    }