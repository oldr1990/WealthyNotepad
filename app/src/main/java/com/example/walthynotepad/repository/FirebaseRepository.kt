package com.example.walthynotepad.repository

import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.data.UserEntries
import com.example.walthynotepad.util.LoginResource
import com.example.walthynotepad.util.NotesResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface FirebaseRepository {
    val authCallBack: StateFlow<LoginResource<Boolean>>
    val _authCallBack: MutableStateFlow<LoginResource<Boolean>>
    val _notepadCallBack: MutableStateFlow<NotesResource<Notes>>
    val notepadCallBack: MutableStateFlow<NotesResource<Notes>>
    suspend fun registerUser (userdata: UserEntries)
    suspend fun loginUser (userdata: UserEntries)
    suspend fun checkLoginState():Boolean
    suspend fun logout():Boolean
    suspend fun addNote(note: Notes)
    suspend fun deleteNote(uid: String,id: String)
    suspend fun getNotes()
    suspend fun getUserUID():String?
    fun getLoginData():UserEntries
    fun setLoginData(userData: UserEntries)
    fun checkLoginData():Boolean
}