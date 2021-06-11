package com.example.walthynotepad.di

import com.example.walthynotepad.data.FirebaseAuthAPI
import com.example.walthynotepad.data.FirebaseFirestoreAPI
import com.example.walthynotepad.util.DispatcherProvider
import com.example.walthynotepad.repository.DefaultFirebaseRepository
import com.example.walthynotepad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton

@Module
@InstallIn(ApplicationComponent::class)
object WelcomeModule {

    @Singleton
    @Provides
    fun provideFirebaseFirestore():FirebaseFirestoreAPI =
    object : FirebaseFirestoreAPI {
        override fun getCollectionReference(): CollectionReference {
            val firestore = FirebaseFirestore.getInstance()
            return firestore.collection("notes")
        }
    }

    @Singleton
    @Provides
    fun provideFirebaseAuthObj(): FirebaseAuthAPI = object : FirebaseAuthAPI {
        override fun auth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(api: FirebaseAuthAPI, firestore: FirebaseFirestoreAPI): FirebaseRepository =
        DefaultFirebaseRepository(api,firestore)

    @Singleton
    @Provides
    fun provideDispatchers(): DispatcherProvider = object : DispatcherProvider {
        override val main: CoroutineDispatcher
            get() = Dispatchers.Main
        override val io: CoroutineDispatcher
            get() = Dispatchers.IO
        override val default: CoroutineDispatcher
            get() = Dispatchers.Default
        override val unconfined: CoroutineDispatcher
            get() = Dispatchers.Unconfined
    }
}