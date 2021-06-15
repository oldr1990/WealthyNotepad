package com.example.walthynotepad.repository


import android.util.Log
import com.example.walthynotepad.data.*
import com.example.walthynotepad.util.LoginResource
import com.example.walthynotepad.util.NotesResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

class DefaultFirebaseRepository @Inject constructor(
    private val authApi: FirebaseAuthAPI,
    private val firestoreAPI: FirebaseFirestoreAPI,
    private val sharedPreferencesAPI: SharedPreferencesAPI
) :
    FirebaseRepository {
    private val sharedPreferences = sharedPreferencesAPI.sharedPreferences
    private var auth = authApi.auth()
    private var firestore = firestoreAPI.getCollectionReference()

    override val _authCallBack = MutableStateFlow<LoginResource<Boolean>>(LoginResource.Empty())
    override val authCallBack: StateFlow<LoginResource<Boolean>> = _authCallBack

    override val _notepadCallBack = MutableStateFlow<NotesResource<Notes>>(NotesResource.Empty())
    override val notepadCallBack: MutableStateFlow<NotesResource<Notes>> = _notepadCallBack

    override suspend fun registerUser(userdata: UserEntries) {
        try {

            CoroutineScope(Dispatchers.IO).launch {
                auth.createUserWithEmailAndPassword(userdata.email, userdata.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch {
                            _authCallBack.value =
                                LoginResource.Success(auth.currentUser?.uid ?: "Error!")
                            Log.e(
                                "!@#",
                                "DefaultFirebaseRepository " + auth.currentUser?.uid ?: "Error!"
                            )
                        }
                        else _authCallBack.value =
                            LoginResource.Error(it.exception?.message.toString())
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
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch {
                            _authCallBack.value =
                                LoginResource.Success(auth.currentUser?.uid ?: "Error!")
                        }
                        else _authCallBack.value =
                            LoginResource.Error(it.exception?.message.toString())
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

    override suspend fun addNote(note: Notes) {
        try {
            if (auth.currentUser != null) {
                note.userUID = auth.currentUser?.uid.toString()
                firestore.add(note)
                    .addOnSuccessListener {
                        _notepadCallBack.value = NotesResource.SuccessAdd()
                    }
                    .addOnFailureListener {
                        _notepadCallBack.value = NotesResource.Error(it.message.toString())
                    }
            } else throw Exception("You are not authorized!")
        } catch (e: Exception) {
            _notepadCallBack.value = NotesResource.Error(e.message.toString())
        }
    }

    override suspend fun deleteNote(uid: String, id: String) {

    }

    override suspend fun getNotes() {
        try {
            if (auth.currentUser != null) {
                firestore.get().addOnSuccessListener {
                    _notepadCallBack.value = NotesResource.Success(it.toObjects(Notes::class.java))
                }
            } else throw Exception("You are not authorized!")
        } catch (e: Exception) {
            _notepadCallBack.value = NotesResource.Error(e.message.toString())
        }
    }

    override suspend fun getUserUID(): String? {
        return auth.currentUser?.uid
    }

    override fun getLoginData(): UserEntries {
        val email = sharedPreferences.getString("Email", null) ?: ""
        val password = sharedPreferences.getString("Password", null) ?: ""
        return UserEntries(email, password)
    }

    override fun setLoginData(userData: UserEntries) {
        val editor = sharedPreferences.edit()
        editor.apply {
            putString("Email", userData.email)
            putString("Password", userData.password)
            apply()
        }
    }

    override fun checkLoginData(): Boolean {
        val getData = getLoginData()
        if (getData.email == "" || getData.password == "") return false
        return true
    }


}