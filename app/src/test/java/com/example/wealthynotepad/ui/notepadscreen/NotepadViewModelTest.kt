package com.example.wealthynotepad.ui.notepadscreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_IMG_URI
import com.example.wealthynotepad.TestConstants.NOTE_ADDED_LABEL
import com.example.wealthynotepad.TestConstants.NOTE_DATE
import com.example.wealthynotepad.TestConstants.NOTE_IMG_URI
import com.example.wealthynotepad.TestConstants.NOTE_TEXT
import com.example.wealthynotepad.TestConstants.TEST_USER_ID
import com.example.wealthynotepad.data.Notes
import com.example.wealthynotepad.ui.welcomescreen.WelcomeVIewModel
import com.example.wealthynotepad.util.DispatcherProvider
import com.example.wealthynotepad.util.FakeRepository
import com.example.wealthynotepad.util.MainCoroutineRule
import com.example.wealthynotepad.util.NotepadEvent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
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


@Config(manifest= Config.NONE)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class NotepadViewModelTest{
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: NotepadViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NotepadViewModel(FakeRepository(),object : DispatcherProvider {
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
        ViewModelStore().clear()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()

    }

    @Test
    fun logoutReturnLogout() = runBlockingTest{
        var answer = false
        runBlocking { viewModel.logout()}
        val event = runBlocking { viewModel.noteCallBack.first() }
        answer = when (event){
            NotepadEvent.Empty -> false
            is NotepadEvent.Failure -> false
            NotepadEvent.Loading -> false
            NotepadEvent.Logout -> true
            is NotepadEvent.Success -> false
            is NotepadEvent.SuccessAddDelete -> false
        }
        assertThat(answer).isTrue()
    }

    @Test
    fun addNoteProperlyReturnSuccess() = runBlockingTest {
        var answer = false
        runBlocking { viewModel.addNote(Notes( NOTE_DATE, NOTE_TEXT, NOTE_IMG_URI, TEST_USER_ID)) }
        val event = runBlocking { viewModel.noteCallBack.first() }
        answer = when(event){
            NotepadEvent.Empty -> false
            is NotepadEvent.Failure -> false
            NotepadEvent.Loading -> false
            NotepadEvent.Logout -> false
            is NotepadEvent.Success -> false
            is NotepadEvent.SuccessAddDelete -> event.message == NOTE_ADDED_LABEL
        }
        assertThat(answer).isTrue()
    }
    @Test
    fun addNoteImproperlyURIReturnFailure() = runBlockingTest {
        var answer = false
        runBlocking { viewModel.addNote(Notes( NOTE_DATE, NOTE_TEXT, "not uri", TEST_USER_ID)) }
        val event = runBlocking { viewModel.noteCallBack.first() }
        answer = when(event){
            NotepadEvent.Empty -> false
            is NotepadEvent.Failure -> event.message == ERROR_WRONG_IMG_URI
            NotepadEvent.Loading -> false
            NotepadEvent.Logout -> false
            is NotepadEvent.Success -> false
            is NotepadEvent.SuccessAddDelete -> false
        }
        assertThat(answer).isTrue()
    }
}