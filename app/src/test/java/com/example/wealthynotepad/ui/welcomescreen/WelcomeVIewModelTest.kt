package com.example.wealthynotepad.ui.welcomescreen


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_EMAIL
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_PASSWORD
import com.example.wealthynotepad.TestConstants.TEST_BAD_EMAIL
import com.example.wealthynotepad.TestConstants.TEST_BAD_PASSWORD
import com.example.wealthynotepad.TestConstants.TEST_EMAIL
import com.example.wealthynotepad.TestConstants.TEST_PASSWORD
import com.example.wealthynotepad.TestConstants.TEST_USER_ID
import com.example.wealthynotepad.data.UserEntries
import com.example.wealthynotepad.util.*
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
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
    fun registerWithImproperlyEmailInputReturnError() = runBlockingTest{
        var answer = false
        runBlocking{ viewModel.register(UserEntries(TEST_BAD_EMAIL, TEST_PASSWORD))}
        val event =  runBlocking{viewModel.loginFlow.first()}
         answer = when (event){
            LoginEvent.Empty -> false
            is LoginEvent.Failure -> event.errorText == ERROR_WRONG_EMAIL
            LoginEvent.Loading -> false
            is LoginEvent.Success -> false
        }
        assertThat(answer).isTrue()
    }
    @Test
    fun registerWithImproperlyPasswordInputReturnError() = runBlockingTest{
        var answer = false
        runBlocking{ viewModel.register(UserEntries(TEST_EMAIL, TEST_BAD_PASSWORD))}
        val event =  runBlocking{viewModel.loginFlow.first()}
         answer = when (event){
            LoginEvent.Empty -> false
            is LoginEvent.Failure -> event.errorText == ERROR_WRONG_PASSWORD
            LoginEvent.Loading -> false
            is LoginEvent.Success -> false
        }
        assertThat(answer).isTrue()
    }

    @Test
    fun registerWithProperlyInputReturnSuccess() = runBlockingTest{
            var answer = false
        runBlocking{ viewModel.register(UserEntries(TEST_EMAIL, TEST_PASSWORD))}
        val state = runBlocking{viewModel.loginFlow.first()}
            answer = when (state){
                LoginEvent.Empty -> false
                is LoginEvent.Failure -> false
                LoginEvent.Loading -> false
                is LoginEvent.Success -> state.uid == TEST_USER_ID
            }
            assertThat(answer).isTrue()
    }
    @Test
    fun loginWithImproperlyEmailInputReturnError() = runBlockingTest{
        var answer = false
        runBlocking{ viewModel.login(UserEntries(TEST_BAD_EMAIL, TEST_PASSWORD))}
        val event =  runBlocking{viewModel.loginFlow.first()}
         answer = when (event){
            LoginEvent.Empty -> false
            is LoginEvent.Failure -> event.errorText == ERROR_WRONG_EMAIL
            LoginEvent.Loading -> false
            is LoginEvent.Success -> false
        }
        assertThat(answer).isTrue()
    }
    @Test
    fun loginWithImproperlyPasswordInputReturnError() = runBlockingTest{
        var answer = false
        runBlocking{ viewModel.login(UserEntries(TEST_EMAIL, TEST_BAD_PASSWORD))}
        val event =  runBlocking{viewModel.loginFlow.first()}
         answer = when (event){
            LoginEvent.Empty -> false
            is LoginEvent.Failure -> event.errorText == ERROR_WRONG_PASSWORD
            LoginEvent.Loading -> false
            is LoginEvent.Success -> false
        }
        assertThat(answer).isTrue()
    }

    @Test
    fun loginWithProperlyInputReturnSuccess() = runBlockingTest{
            var answer = false
        runBlocking{ viewModel.login(UserEntries(TEST_EMAIL, TEST_PASSWORD))}
        val state = runBlocking{viewModel.loginFlow.first()}
            answer = when (state){
                LoginEvent.Empty -> false
                is LoginEvent.Failure -> false
                LoginEvent.Loading -> false
                is LoginEvent.Success -> state.uid == TEST_USER_ID
            }
            assertThat(answer).isTrue()
    }
}