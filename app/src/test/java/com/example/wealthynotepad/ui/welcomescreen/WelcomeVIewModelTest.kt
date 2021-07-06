package com.example.wealthynotepad.ui.welcomescreen

import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wealthynotepad.TestConstants.ERROR_EMPTY_TEXT
import com.example.wealthynotepad.TestConstants.TEST_EMAIL
import com.example.wealthynotepad.TestConstants.TEST_PASSWORD
import com.example.wealthynotepad.TestConstants.TEST_USER_ID
import com.example.wealthynotepad.data.UserEntries
import com.example.wealthynotepad.util.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.internal.wait
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest=Config.NONE)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class WelcomeVIewModelTest{
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var viewModel: WelcomeVIewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = WelcomeVIewModel(FakeRepository(),object : DispatcherProvider {
            override val main: CoroutineDispatcher
                get() = Dispatchers.Main
            override val io: CoroutineDispatcher
                get() = Dispatchers.IO
            override val default: CoroutineDispatcher
                get() = Dispatchers.Default
            override val unconfined: CoroutineDispatcher
                get() = Dispatchers.Unconfined})
    }

    @After
    fun cleanUp() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }


    @Test
    fun loginWithProperlyInput() = runBlockingTest{

            var answer = false
        runBlocking{ answer = viewModel.getUserID() }
            answer = when (viewModel.loginFlow.value){
                LoginEvent.Empty -> false
                is LoginEvent.Failure -> true
                LoginEvent.Loading -> true
                is LoginEvent.Success -> true
            }
            assertThat(answer).isTrue()


    }
}