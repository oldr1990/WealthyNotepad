package com.example.walthynotepad.data

import com.google.firebase.firestore.CollectionReference

interface FirebaseFirestoreAPI {
    fun getCollectionReference():CollectionReference
}