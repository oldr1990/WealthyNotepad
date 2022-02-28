package com.example.wealthynotepad.util

import com.example.wealthynotepad.TestConstants.EMPTY_STRING
import com.example.wealthynotepad.TestConstants.ERROR_EMPTY_TEXT
import com.example.wealthynotepad.TestConstants.ERROR_NOTE_DOESNT_EXIST
import com.example.wealthynotepad.TestConstants.ERROR_NOT_AUTHORIZED
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_DATE
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_EMAIL
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_IMG_URI
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_PASSWORD
import com.example.wealthynotepad.TestConstants.NOTE_IMG_URI
import com.example.wealthynotepad.TestConstants.TEST_EMAIL
import com.example.wealthynotepad.TestConstants.TEST_PASSWORD
import com.example.wealthynotepad.TestConstants.TEST_USER_ID
import com.example.wealthynotepad.data.Notes
import com.example.wealthynotepad.data.UserEntries
import com.example.wealthynotepad.repository.FirebaseRepository
import com.example.wealthynotepad.ui.welcomescreen.LoginResource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakeRepository : FirebaseRepository {
    private val notes = mutableListOf<Notes>()
    private val _authCallBack = MutableStateFlow<LoginResource<Boolean>>(LoginResource.Empty())
    override val authCallBack: StateFlow<LoginResource<Boolean>> = _authCallBack
    private var isRegistered = false
    private var isSetData = false
    private val _notepadCallBack = MutableStateFlow<NotesResource<Notes>>(NotesResource.Empty())
    override val notepadCallBack: MutableStateFlow<NotesResource<Notes>> = _notepadCallBack

    override suspend fun registerUser(userdata: UserEntries) {
        if (isItEmail(userdata.email)) {
            if (userdata.password.length < 4 || userdata.password.length > 16) _authCallBack.value =
                LoginResource.Error(
                    ERROR_WRONG_PASSWORD
                )
            else {
                isRegistered = true
                _authCallBack.value = LoginResource.Success(TEST_USER_ID)
            }
        } else _authCallBack.value = LoginResource.Error(ERROR_WRONG_EMAIL)
    }

    override suspend fun loginUser(userdata: UserEntries) {
        if (isItEmail(userdata.email)) {
            if (userdata.password.length < 4 || userdata.password.length > 16) _authCallBack.value =
                LoginResource.Error(
                    ERROR_WRONG_PASSWORD
                )
            else {
                isRegistered = true
                _authCallBack.value = LoginResource.Success(TEST_USER_ID)
            }
        } else _authCallBack.value = LoginResource.Error(ERROR_WRONG_EMAIL)

    }

    override suspend fun checkLoginState(): Boolean {
        isRegistered = true
        return isRegistered
    }

    override suspend fun logout() {
        isRegistered = false
        _notepadCallBack.value = NotesResource.Logout()
    }

    override suspend fun addNote(note: Notes) {
        if (isRegistered) {
            if (note.text != EMPTY_STRING) {
                if (note.date.toLongOrNull() != null) {
                    if (note.img == NOTE_IMG_URI || note.img == EMPTY_STRING){
                        if(note.userUID == TEST_USER_ID){
                            notes.add(note)
                            _notepadCallBack.value = NotesResource.Success(notes)
                            _notepadCallBack.value = NotesResource.SuccessAdd()
                        }
                        else _notepadCallBack.value = NotesResource.Error(ERROR_NOT_AUTHORIZED)
                    }else _notepadCallBack.value = NotesResource.Error(ERROR_WRONG_IMG_URI)
                } else _notepadCallBack.value = NotesResource.Error(ERROR_WRONG_DATE)
            } else _notepadCallBack.value = NotesResource.Error(ERROR_EMPTY_TEXT)
        } else _notepadCallBack.value = NotesResource.Error(ERROR_NOT_AUTHORIZED)
    }

    override suspend fun deleteNote(note: Notes) {
        if (isRegistered) {
            if (notes.contains(note)) {
                notes.remove(note)
                _notepadCallBack.value = NotesResource.Success(notes)
                _notepadCallBack.value = NotesResource.SuccessDelete()
            } else _notepadCallBack.value = NotesResource.Error(ERROR_NOTE_DOESNT_EXIST)
        } else _notepadCallBack.value = NotesResource.Error(ERROR_NOT_AUTHORIZED)
    }

    override suspend fun getNotes(uid: String) {
       if(uid == TEST_USER_ID) _notepadCallBack.value  = NotesResource.Success(notes)
        else _notepadCallBack.value = NotesResource.Error(ERROR_NOT_AUTHORIZED)
    }

    override suspend fun getUserUID(): String? {
        return if (isRegistered) TEST_USER_ID
        else null
    }

    override fun getLoginData(): UserEntries {
        return if (checkLoginData()) UserEntries(TEST_EMAIL, TEST_PASSWORD)
        else UserEntries("", "")
    }

    override fun setLoginData(userData: UserEntries) {
        isSetData = true
    }

    override fun checkLoginData(): Boolean {
        _authCallBack.value = LoginResource.Error(EMPTY_STRING)
        return isSetData
    }
}