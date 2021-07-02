package com.example.wealthynotepad.data

import com.google.firebase.firestore.CollectionReference

interface FirebaseFirestoreAPI {
    fun getCollectionReference():CollectionReference
}