package com.example.wealthynotepad.repository

import androidx.core.net.toUri
import com.example.wealthynotepad.data.*
import com.example.wealthynotepad.data.Constants.EMAIL_LABEL
import com.example.wealthynotepad.data.Constants.EMPTY_STRING
import com.example.wealthynotepad.data.Constants.PASSWORD_LABEL
import com.example.wealthynotepad.util.DispatcherProvider
import com.example.wealthynotepad.ui.welcomescreen.LoginResource
import com.example.wealthynotepad.ui.notepadscreen.NotesResource
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
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
    private val storage = Firebase.storage
    private val storageReference = storage.reference

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
                        _authCallBack.value =  if (it.isSuccessful) {
                            LoginResource.Success(
                                auth.currentUser?.uid ?: Constants.ERROR_YOU_ARE_NOT_AUTHORIZED)
                        }
                        else {
                            LoginResource.Error(it.exception?.message.toString())
                        }
                    }
            }
        } catch (e: Exception) {

            _authCallBack.value = LoginResource.Error(e.message.toString())
        }
    }

    override suspend fun checkLoginState(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun logout() {
        auth.signOut()
        if (!checkLoginState()) {
            sharedPreferences.edit().apply {
                this.putString(EMAIL_LABEL, EMPTY_STRING)
                this.putString(PASSWORD_LABEL, EMPTY_STRING)
                apply()
            }
            _authCallBack.value  = LoginResource.Empty()
            _notepadCallBack.value = NotesResource.Logout()
        }
    }

    override suspend fun addNote(note: Notes) {
        try {
            if (auth.currentUser != null) {
                note.userUID = auth.currentUser?.uid.toString()
                note.img = imageUploader(note.img)
                firestore.add(note)
                    .addOnSuccessListener {
                        _notepadCallBack.value = NotesResource.SuccessAdd()
                    }
                    .addOnFailureListener {
                        _notepadCallBack.value = NotesResource.Error(it.message.toString())
                    }
            } else throw Exception(Constants.ERROR_YOU_ARE_NOT_AUTHORIZED)
        } catch (e: Exception) {
            _notepadCallBack.value = NotesResource.Error(e.message.toString())
        }
    }

    private suspend fun imageUploader(filename: String): String {
        var url = EMPTY_STRING
        if (filename == EMPTY_STRING) return url
        try {
            val ref = storageReference
                .child(Constants.FIRESTORE_IMAGE_DIRECTORY + filename.hashCode().toString())
            val uploadTask = ref.putFile(filename.toUri())
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        url = task.result.toString()
                    } else {
                        _notepadCallBack.value =
                            NotesResource.Error(Constants.ERROR_IMAGE_UPLOADING)
                    }
                }.await()

        } catch (e: Exception) {
            _notepadCallBack.value = NotesResource.Error(e.message.toString())
            return url
        }
        return url
    }

    override suspend fun deleteNote(note: Notes) {
        if (deleteImage(note.img))
            firestore
                .whereEqualTo(Constants.FIRESTORE_FIELD_DATE, note.date)
                .whereEqualTo(Constants.FIRESTORE_FIELD_IMG_URL, note.img)
                .whereEqualTo(Constants.FIRESTORE_FIEL_TEXT, note.text)
                .whereEqualTo(Constants.FIRESTORE_FIELD_USER_ID, note.userUID).get()
                .addOnSuccessListener {
                    if (!it.isEmpty) {
                        firestore.document(it.documents[0].id).delete().addOnSuccessListener {
                            _notepadCallBack.value = NotesResource.SuccessDelete()
                        }
                            .addOnFailureListener { e->
                                _notepadCallBack.value =
                                    NotesResource.Error(e.message.toString())
                            }
                    } else _notepadCallBack.value =
                        NotesResource.Error(Constants.ERROR_NOTE_CANT_FIND_NOTE)
                }
    }

    private suspend fun deleteImage(url: String): Boolean {
        if (url == EMPTY_STRING) return true
        var isDeleted = false
        val imageReference = storage.getReferenceFromUrl(url)
        try {
            imageReference.delete().addOnCompleteListener {
                if (it.isSuccessful) isDeleted = true
            }
                .await()
        } catch (e: Exception) {
            isDeleted = false
            _notepadCallBack.value = NotesResource.Error(e.message.toString())
        }
        return isDeleted
    }

    override suspend fun getNotes(uid: String) {
        firestore
            .whereEqualTo(Constants.FIRESTORE_FIELD_USER_ID, uid)
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    _notepadCallBack.value = NotesResource.Error(error.message.toString())
                    return@addSnapshotListener                                                  //останавливает слушатель
                }
                if (snapshot != null) {
                    _notepadCallBack.value =
                        NotesResource.Success(snapshot.toObjects(Notes::class.java))
                }
            }

    }


    override suspend fun getUserUID(): String? {
        return auth.currentUser?.uid
    }

    override fun getLoginData(): UserEntries {
        val email = sharedPreferences.getString(EMAIL_LABEL, null) ?: ""
        val password = sharedPreferences.getString(PASSWORD_LABEL, null) ?: ""
        return UserEntries(email, password)
    }

    override fun setLoginData(userData: UserEntries) {
        sharedPreferences.edit().apply {
            putString(EMAIL_LABEL, userData.email)
            putString(PASSWORD_LABEL, userData.password)
            apply()
        }
    }

    override fun checkLoginData(): Boolean {
        val getData = getLoginData()
        if (getData.email == "" || getData.password == "") return false
        return true
    }


}