package com.example.wealthynotepad.repository

import com.example.wealthynotepad.data.Notes
import com.example.wealthynotepad.data.UserEntries
import com.example.wealthynotepad.ui.welcomescreen.NetworkResponse
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    suspend fun registerUser (userData: UserEntries): Flow<NetworkResponse<String>>
    suspend fun loginUser (userData: UserEntries): Flow<NetworkResponse<String>>
    suspend fun checkLoginState(): Boolean
    suspend fun logout(): Flow<NetworkResponse<Unit>>
    suspend fun addNote(note: Notes): Flow<NetworkResponse<String>>
    suspend fun deleteNote(note: Notes): Flow<NetworkResponse<String>>
    suspend fun getNotes( callBack: (NetworkResponse<List<Notes>>) -> Unit)
    suspend fun getUserUID():String?
    fun getLoginData():UserEntries
    fun setLoginData(userData: UserEntries)
    fun checkLoginData():Boolean
}