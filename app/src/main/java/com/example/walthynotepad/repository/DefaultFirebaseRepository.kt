package com.example.walthynotepad.repository


import android.util.Log
import com.example.walthynotepad.data.FirebaseAuthObj
import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.data.UserEntries
import com.example.walthynotepad.util.LoginResource
import com.example.walthynotepad.util.NotesResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

class DefaultFirebaseRepository @Inject constructor(private val authApi: FirebaseAuthObj) :
    FirebaseRepository {
    private var auth = authApi.auth()
    override val _authCallBack = MutableStateFlow<LoginResource<Boolean>>(LoginResource.Empty())
    override val authCallBack: StateFlow<LoginResource<Boolean>> = _authCallBack
    override val _notepadCallBack = MutableStateFlow<NotesResource<Notes>>(NotesResource.Empty())
    override val notepadCallBack: MutableStateFlow<NotesResource<Notes>> = _notepadCallBack

    override suspend fun registerUser(userdata: UserEntries) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                auth.createUserWithEmailAndPassword(userdata.email, userdata.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch{
                            _authCallBack.value =
                                LoginResource.Success(auth.currentUser?.uid ?: "Error!")
                            Log.e("!@#", "DefaultFirebaseRepository " +auth.currentUser?.uid?:"Error!")
                        }

                        else _authCallBack.value = LoginResource.Error(it.exception?.message.toString())
                        Log.e("!@#", "Register complete!}")
                    }

            }

        } catch (e: Exception) {
            Log.e("!@#", "Exception:  ${e.message}")
            _authCallBack.value = LoginResource.Error(e.message.toString())
        }
    }

    override suspend fun loginUser(userdata: UserEntries) {
        try {
            CoroutineScope(Dispatchers.IO).launch {
                auth.signInWithEmailAndPassword(userdata.email, userdata.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch{
                            _authCallBack.value =
                                LoginResource.Success(auth.currentUser?.uid ?: "Error!")
                        }

                        else _authCallBack.value = LoginResource.Error(it.exception?.message.toString())
                        Log.e("!@#", "Login complete!}")
                    }

            }

        } catch (e: Exception) {
            Log.e("!@#", "Exception:  ${e.message}")
            _authCallBack.value = LoginResource.Error(e.message.toString())
        }
    }

    override suspend fun checkLoginState(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun logout(): Boolean {
        auth.signOut()
        return !checkLoginState()
    }

    override suspend fun addNote(uid: String, note: Notes): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun deleteNote(uid: String, id: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getNotes(uid: String): List<Notes> {
        TODO("Not yet implemented")
    }


}