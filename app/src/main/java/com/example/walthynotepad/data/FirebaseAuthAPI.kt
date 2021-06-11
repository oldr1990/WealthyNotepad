package com.example.walthynotepad.data

import com.google.firebase.auth.FirebaseAuth

interface FirebaseAuthAPI {
    fun auth(): FirebaseAuth
}
