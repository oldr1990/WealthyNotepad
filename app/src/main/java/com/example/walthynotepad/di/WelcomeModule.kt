package com.example.walthynotepad.di

import com.example.walthynotepad.data.FirebaseAuthObj
import com.example.walthynotepad.util.DispatcherProvider
import com.example.walthynotepad.repository.DefaultFirebaseRepository
import com.example.walthynotepad.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
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
    fun provideFirebaseAuthObj():FirebaseAuthObj = object : FirebaseAuthObj{
        override fun auth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }
    }

    @Singleton
    @Provides
    fun provideFirebaseAuth(api:FirebaseAuthObj ): FirebaseRepository = DefaultFirebaseRepository(api)

    @Singleton
    @Provides
    fun provideDispatchers():DispatcherProvider = object : DispatcherProvider{
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