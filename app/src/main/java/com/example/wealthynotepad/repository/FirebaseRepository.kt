package com.example.wealthynotepad.repository

import com.example.wealthynotepad.data.Notes
import com.example.wealthynotepad.data.UserEntries
import com.example.wealthynotepad.ui.welcomescreen.LoginResource
import com.example.wealthynotepad.ui.notepadscreen.NotesResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

interface FirebaseRepository {
    val authCallBack: StateFlow<LoginResource<Boolean>>
    val notepadCallBack: MutableStateFlow<NotesResource<Notes>>
    fun registerUser (userdata: UserEntries)
    suspend fun loginUser (userdata: UserEntries)
    suspend fun checkLoginState():Boolean
    suspend fun logout()
    suspend fun addNote(note: Notes)
    suspend fun deleteNote(note: Notes)
    suspend fun getNotes(uid: String)
    suspend fun getUserUID():String?
    fun getLoginData():UserEntries
    fun setLoginData(userData: UserEntries)
    fun checkLoginData():Boolean
}