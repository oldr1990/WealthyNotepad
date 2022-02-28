package com.example.wealthynotepad.di

import android.content.Context
import android.content.SharedPreferences
import com.example.wealthynotepad.data.Constants
import com.example.wealthynotepad.data.FirebaseAuthAPI
import com.example.wealthynotepad.data.FirebaseFirestoreAPI
import com.example.wealthynotepad.data.SharedPreferencesAPI
import com.example.wealthynotepad.util.DispatcherProvider
import com.example.wealthynotepad.repository.DefaultFirebaseRepository
import com.example.wealthynotepad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object WelcomeModule {

    @Provides
    fun provideFirebaseFirestore(): FirebaseFirestoreAPI =
        object : FirebaseFirestoreAPI {
            override fun getCollectionReference(): CollectionReference {
                val firestore = FirebaseFirestore.getInstance()
                return firestore.collection(Constants.FIRESTORE_FIELD_NOTE_TABLE)
            }
        }

    @Provides
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferencesAPI {
        return object : SharedPreferencesAPI {
            override val sharedPreferences: SharedPreferences = context.getSharedPreferences(Constants.SHARED_PREF_NAME, Context.MODE_PRIVATE)
        }
    }


    @Provides
    fun provideFirebaseAuthObj(): FirebaseAuthAPI = object : FirebaseAuthAPI {
        override fun auth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }
    }


    @Provides
    fun provideFirebaseAuth(
        api: FirebaseAuthAPI,
        firestore: FirebaseFirestoreAPI,
        sharedPreferences: SharedPreferencesAPI
    ): FirebaseRepository =
        DefaultFirebaseRepository(api, firestore, sharedPreferences )


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