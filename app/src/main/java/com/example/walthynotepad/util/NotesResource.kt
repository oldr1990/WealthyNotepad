package com.example.walthynotepad.util

import com.example.walthynotepad.data.Notes

sealed class NotesResource<T>(val data: Notes?, message: String?) {
        class Success<T>(data: Notes) : NotesResource<T>(data, null)
        class Error<T>(message: String) : NotesResource<T>(null, message)
        class Empty<T>() : NotesResource<T>(null, null )
    }