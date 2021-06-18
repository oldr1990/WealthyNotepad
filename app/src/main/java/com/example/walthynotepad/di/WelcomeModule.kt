package com.example.walthynotepad.di

import android.content.Context
import android.content.SharedPreferences
import com.example.walthynotepad.data.Constants
import com.example.walthynotepad.data.FirebaseAuthAPI
import com.example.walthynotepad.data.FirebaseFirestoreAPI
import com.example.walthynotepad.data.SharedPreferencesAPI
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
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton


@InstallIn(ApplicationComponent::class)
@Module
object WelcomeModule {

    @Singleton
    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestoreAPI =
        object : FirebaseFirestoreAPI {
            override fun getCollectionReference(): CollectionReference {
                val firestore = FirebaseFirestore.getInstance()
                return firestore.collection(Constants.firestoreFieldNoteTable)
            }
        }

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferencesAPI {
        return object : SharedPreferencesAPI {
            override val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.sharedPreferencesName, Context.MODE_PRIVATE)
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
    fun provideFirebaseAuth(
        api: FirebaseAuthAPI,
        firestore: FirebaseFirestoreAPI,
        sharedPreferences: SharedPreferencesAPI,
        dispatcher: DispatcherProvider
    ): FirebaseRepository =
        DefaultFirebaseRepository(api, firestore, sharedPreferences,dispatcher )

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