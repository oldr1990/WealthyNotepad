package com.example.wealthynotepad.data

import com.google.firebase.auth.FirebaseAuth

interface FirebaseAuthAPI {
    fun auth(): FirebaseAuth
}
