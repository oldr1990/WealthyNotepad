package com.example.wealthynotepad.repository

import androidx.core.net.toUri
import com.example.wealthynotepad.data.*
import com.example.wealthynotepad.data.Constants.EMAIL_LABEL
import com.example.wealthynotepad.data.Constants.EMPTY_STRING
import com.example.wealthynotepad.data.Constants.PASSWORD_LABEL
import com.example.wealthynotepad.ui.welcomescreen.NetworkResponse
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import okhttp3.internal.wait
import javax.inject.Inject
import kotlin.Exception

class DefaultFirebaseRepository @Inject constructor(
    authApi: FirebaseAuthAPI,
    firestoreAPI: FirebaseFirestoreAPI,
    sharedPreferencesAPI: SharedPreferencesAPI
) :
    FirebaseRepository {
    private val sharedPreferences = sharedPreferencesAPI.sharedPreferences
    private var auth = authApi.auth()
    private var firestore = firestoreAPI.getCollectionReference()
    private val storage = Firebase.storage
    private val storageReference = storage.reference

    override suspend fun registerUser(userData: UserEntries): Flow<NetworkResponse<String>> = flow {
        try {
            val result = auth.createUserWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {}
            result.await()
            if (result.isSuccessful) {
                emit(NetworkResponse.Success(auth.currentUser?.uid ?: "Error!"))
            } else {
                emit(NetworkResponse.Error(result.exception?.message ?: "Error!"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Error(e.message.toString()))
        }
    }

    override suspend fun loginUser(userData: UserEntries): Flow<NetworkResponse<String>> = flow {
        try {
            val result = auth.signInWithEmailAndPassword(userData.email, userData.password)
                .addOnCompleteListener {}
            result.await()
            if (result.isSuccessful) {
                emit(NetworkResponse.Success(auth.currentUser?.uid ?: "No uid in response!"))
            } else {
                emit(NetworkResponse.Error(result.exception?.message ?: "Error from server!"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Error(e.message ?: "Exception error!"))
        }
    }

    override suspend fun checkLoginState(): Boolean {
        return auth.currentUser != null
    }

    override suspend fun logout(): Flow<NetworkResponse<Unit>> = flow {
        auth.signOut()
        if (!checkLoginState()) {
            sharedPreferences.edit().apply {
                this.putString(EMAIL_LABEL, EMPTY_STRING)
                this.putString(PASSWORD_LABEL, EMPTY_STRING)
                apply()
            }
            emit(NetworkResponse.Success(Unit))
        } else {
            emit(NetworkResponse.Error(Unit))
        }
    }

    override suspend fun addNote(note: Notes): Flow<NetworkResponse<String>> = flow {
        try {
            if (auth.currentUser == null) {
                emit(NetworkResponse.Error("You are not authorized!"))
            }
            note.userUID = auth.currentUser?.uid.toString()
            imageUploader(note.img).collect {
                when (it) {
                    is NetworkResponse.Error -> {
                        emit(it)
                    }
                    is NetworkResponse.Success -> {
                        val response = firestore.add(note.copy(img = it.data)).addOnCompleteListener { }
                        response.await()
                        if (response.isSuccessful) {
                            emit(NetworkResponse.Success("Successful added"))
                        } else {
                            emit(NetworkResponse.Error(response.exception?.message ?: "Error"))
                        }
                    }
                }
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Error(e.message ?: "Error"))
        }
    }

    private suspend fun imageUploader(filename: String): Flow<NetworkResponse<String>> = flow {
        try {
            if (filename.isEmpty()) {
                emit(NetworkResponse.Success(""))
                return@flow
            }
            val ref = storageReference
                .child(Constants.FIRESTORE_IMAGE_DIRECTORY + filename.hashCode().toString())
            val uploadTask = ref.putFile(filename.toUri())
            uploadTask.await()
            val task = uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                ref.downloadUrl
            }.addOnCompleteListener {}
            task.await()
            if (task.isSuccessful) {
                emit(NetworkResponse.Success(task.result.toString()))
                return@flow
            } else {
                emit(NetworkResponse.Error(Constants.ERROR_IMAGE_UPLOADING))
                return@flow
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Error(e.message.toString()))
            return@flow
        }
    }

    override suspend fun deleteNote(note: Notes): Flow<NetworkResponse<String>> = flow {
        try {
            if (deleteImage(note.img)) {
                val response = firestore
                    .whereEqualTo(Constants.FIRESTORE_FIELD_DATE, note.date)
                    .whereEqualTo(Constants.FIRESTORE_FIELD_IMG_URL, note.img)
                    .whereEqualTo(Constants.FIRESTORE_FIEL_TEXT, note.text)
                    .whereEqualTo(Constants.FIRESTORE_FIELD_USER_ID, note.userUID).get()
                    .addOnSuccessListener {}
                response.await()
                if (response.result?.isEmpty != true) {
                    val deleteResponse =
                        firestore.document(response.result!!.documents[0].id).delete()
                            .addOnSuccessListener {}
                    deleteResponse.await()
                    if (deleteResponse.isSuccessful) {
                        emit(NetworkResponse.Success("Item was successfully deleted!"))
                    } else {
                        emit(NetworkResponse.Error(deleteResponse.exception?.message ?: "Error"))
                    }
                } else emit(NetworkResponse.Error(response.exception?.message ?: "Error"))
            }
        } catch (e: Exception) {
            emit(NetworkResponse.Error(e.message ?: "Error"))
        }
    }

    private fun deleteImage(url: String): Boolean {
        if (url.isEmpty()) return true
        return try {
            storage
                .getReferenceFromUrl(url)
                .delete()
                .addOnCompleteListener {}
                .isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getNotes(callBack: (NetworkResponse<List<Notes>>) -> Unit) {
        firestore
            .whereEqualTo(Constants.FIRESTORE_FIELD_USER_ID, getUserUID().toString())
            .addSnapshotListener { snapshot, error ->
                error?.let {
                    callBack(NetworkResponse.Error(emptyList()))
                    return@addSnapshotListener                                                  //останавливает слушатель
                }
                if (snapshot != null) {
                    callBack(NetworkResponse.Success(snapshot.toObjects(Notes::class.java)))
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