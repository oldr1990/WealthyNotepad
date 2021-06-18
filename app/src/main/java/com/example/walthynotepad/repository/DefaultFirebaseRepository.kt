package com.example.walthynotepad.repository


import android.util.Log
import com.example.walthynotepad.data.*
import com.example.walthynotepad.util.DispatcherProvider
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
    private val sharedPreferencesAPI: SharedPreferencesAPI,
    private val dispatcher: DispatcherProvider
) :
    FirebaseRepository {
    private val sharedPreferences = sharedPreferencesAPI.sharedPreferences
    private var auth = authApi.auth()
    private var firestore = firestoreAPI.getCollectionReference()


    private val _authCallBack = MutableStateFlow<LoginResource<Boolean>>(LoginResource.Empty())
    override val authCallBack: StateFlow<LoginResource<Boolean>> = _authCallBack

    private val _notepadCallBack = MutableStateFlow<NotesResource<Notes>>(NotesResource.Empty())
    override val notepadCallBack: MutableStateFlow<NotesResource<Notes>> = _notepadCallBack

    override suspend fun registerUser(userdata: UserEntries) {
        try {
            CoroutineScope(dispatcher.io).launch {
                auth.createUserWithEmailAndPassword(userdata.email, userdata.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch {
                            _authCallBack.value =
                                LoginResource.Success(auth.currentUser?.uid ?: "Error!")
                        }
                        else _authCallBack.value =
                            LoginResource.Error(it.exception?.message.toString())
                    }

            }

        } catch (e: Exception) {

            _authCallBack.value = LoginResource.Error(e.message.toString())
        }
    }

    override suspend fun loginUser(userdata: UserEntries) {
        try {
            CoroutineScope(dispatcher.io).launch {
                auth.signInWithEmailAndPassword(userdata.email, userdata.password)
                    .addOnCompleteListener {
                        if (it.isSuccessful) CoroutineScope(Dispatchers.IO).launch {
                            _authCallBack.value =
                                LoginResource.Success(
                                    auth.currentUser?.uid ?: Constants.errorYouAreNotAuthorized
                                )
                        }
                        else _authCallBack.value =
                            LoginResource.Error(it.exception?.message.toString())

                    }

            }

        } catch (e: Exception) {

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
            } else throw Exception(Constants.errorYouAreNotAuthorized)
        } catch (e: Exception) {
            _notepadCallBack.value = NotesResource.Error(e.message.toString())
        }
    }

    override suspend fun deleteNote(note: Notes) {
        firestore
            .whereEqualTo(Constants.firestoreFieldDate, note.date)
            .whereEqualTo(Constants.firestoreFieldImgURL, note.img)
            .whereEqualTo(Constants.firestoreFieldText, note.text)
            .whereEqualTo(Constants.firestoreFieldUserID, note.userUID).get().addOnSuccessListener {
                if (!it.isEmpty) {
                    firestore.document(it.documents[0].id).delete()
                } else _notepadCallBack.value = NotesResource.Error(Constants.errorNoteDidntFinded)
            }
    }

    override suspend fun getNotes(uid: String) {
        try {
            if (auth.currentUser != null) {
                firestore.whereEqualTo(Constants.firestoreFieldUserID, uid).get()
                    .addOnSuccessListener {
                        _notepadCallBack.value =
                            NotesResource.Success(it.toObjects(Notes::class.java))
                    }
            } else throw Exception(Constants.errorYouAreNotAuthorized)
        } catch (e: Exception) {
            _notepadCallBack.value = NotesResource.Error(e.message.toString())
        }
    }

    override suspend fun getUserUID(): String? {
        return auth.currentUser?.uid
    }

    override fun getLoginData(): UserEntries {
        val email = sharedPreferences.getString(Constants.email, null) ?: ""
        val password = sharedPreferences.getString(Constants.password, null) ?: ""
        return UserEntries(email, password)
    }

    override fun setLoginData(userData: UserEntries) {
        sharedPreferences.edit().apply {
            putString(Constants.email, userData.email)
            putString(Constants.password, userData.password)
            apply()
        }
    }

    override fun checkLoginData(): Boolean {
        val getData = getLoginData()
        if (getData.email == "" || getData.password == "") return false
        return true
    }


}