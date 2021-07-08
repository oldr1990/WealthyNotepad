package com.example.wealthynotepad.ui.notepadscreen

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.wealthynotepad.TestConstants.EMPTY_STRING
import com.example.wealthynotepad.TestConstants.ERROR_DATE
import com.example.wealthynotepad.TestConstants.ERROR_EMPTY_TEXT
import com.example.wealthynotepad.TestConstants.ERROR_NOT_AUTHORIZED
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_DATE
import com.example.wealthynotepad.TestConstants.ERROR_WRONG_IMG_URI
import com.example.wealthynotepad.TestConstants.NOTE_ADDED_LABEL
import com.example.wealthynotepad.TestConstants.NOTE_DATE
import com.example.wealthynotepad.TestConstants.NOTE_DELETED_LABEL
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
import okhttp3.internal.wait
import org.junit.*
import org.junit.runner.OrderWith
import org.junit.runner.RunWith
import org.junit.runner.manipulation.Ordering
import org.junit.runners.MethodSorters
import org.robolectric.annotation.Config

@FixMethodOrder(MethodSorters.DEFAULT)
@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
class NotepadViewModelTest {

    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: NotepadViewModel
    private val testDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = NotepadViewModel(FakeRepository(), object : DispatcherProvider {
            override val main: CoroutineDispatcher = Dispatchers.Main
            override val io: CoroutineDispatcher = Dispatchers.Main
            override val default: CoroutineDispatcher = Dispatchers.Main
            override val unconfined: CoroutineDispatcher = Dispatchers.Main
        })
    }

    @After
    fun cleanUp() {
        ViewModelStore().clear()
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()

    }


    @Test
    fun a_addNoteProperlyReturnSuccess() = runBlockingTest {
        var answer = false
        val note = Notes( NOTE_DATE, NOTE_TEXT, NOTE_IMG_URI, TEST_USER_ID)
        runBlocking(Dispatchers.Main) {
            viewModel.addNote(note)
        }
        if(viewModel.listOfNotes.value.contains(note)){
            val event = runBlocking(Dispatchers.Main) { viewModel.noteCallBack.first() }
            answer = when (event) {
                NotepadEvent.Empty -> false
                is NotepadEvent.Failure -> false
                NotepadEvent.Loading -> false
                NotepadEvent.Logout -> false
                is NotepadEvent.Success -> false
                is NotepadEvent.SuccessAddDelete -> true
            }
        }
        assertThat(answer).isTrue()
    }

    @Test
    fun d_AddNoteImproperlyURIReturnFailure() = runBlockingTest {
        var answer = false
        runBlocking(Dispatchers.Main) {
            viewModel.addNote(
                Notes(
                    NOTE_DATE,
                    NOTE_TEXT,
                    "not uri",
                    TEST_USER_ID
                )
            )
        }
        val event = runBlocking { viewModel.noteCallBack.value }
        answer = when (event) {
            NotepadEvent.Empty -> false
            is NotepadEvent.Failure -> event.message == ERROR_WRONG_IMG_URI
            NotepadEvent.Loading -> false
            NotepadEvent.Logout -> false
            is NotepadEvent.Success -> false
            is NotepadEvent.SuccessAddDelete -> false
        }
        assertThat(answer).isTrue()
    }

    @Test
    fun b_AddNoteImproperlyTextReturnFailure() = runBlockingTest {
        var answer = false
        runBlocking(Dispatchers.Main) {
            viewModel.addNote(
                Notes(
                    NOTE_DATE,
                    EMPTY_STRING,
                    NOTE_IMG_URI,
                    TEST_USER_ID
                )
            )
        }
        val event = runBlocking { viewModel.noteCallBack.value }
        answer = when (event) {
            NotepadEvent.Empty -> false
            is NotepadEvent.Failure -> event.message == ERROR_EMPTY_TEXT
            NotepadEvent.Loading -> false
            NotepadEvent.Logout -> false
            is NotepadEvent.Success -> false
            is NotepadEvent.SuccessAddDelete -> false
        }
        assertThat(answer).isTrue()
    }

    @Test
    fun c_AddNoteImproperlyUserIDReturnFailure() = runBlockingTest {
        var answer = false
        runBlocking(Dispatchers.Main) {
            viewModel.addNote(
                Notes(
                    NOTE_DATE,
                    NOTE_TEXT,
                    NOTE_IMG_URI,
                    EMPTY_STRING
                )
            )
        }
        val event = runBlocking { viewModel.noteCallBack.value }
        answer = when (event) {
            NotepadEvent.Empty -> false
            is NotepadEvent.Failure -> event.message == ERROR_NOT_AUTHORIZED
            NotepadEvent.Loading -> false
            NotepadEvent.Logout -> false
            is NotepadEvent.Success -> false
            is NotepadEvent.SuccessAddDelete -> false
        }
        assertThat(answer).isTrue()
    }

    @Test
    fun e_AddNoteImproperlyDateReturnFailure() = runBlockingTest {
        var answer = false
        runBlocking(Dispatchers.Main) {
            viewModel.addNote(
                Notes(
                    "NOTE_TEXT",
                    NOTE_TEXT,
                    NOTE_IMG_URI,
                    TEST_USER_ID
                )
            )
        }
        val event = runBlocking { viewModel.noteCallBack.value }
        answer = when (event) {
            NotepadEvent.Empty -> false
            is NotepadEvent.Failure -> event.message == ERROR_DATE
            NotepadEvent.Loading -> false
            NotepadEvent.Logout -> false
            is NotepadEvent.Success -> false
            is NotepadEvent.SuccessAddDelete -> false
        }
        assertThat(answer).isTrue()
    }

    @Test
    fun f_AddDeleteNoteDateReturnTrue() = runBlockingTest {
        var answer = false
        val note = Notes(NOTE_DATE, "My super important note", NOTE_IMG_URI, TEST_USER_ID)
        runBlocking(Dispatchers.Main) {
            viewModel.addNote(note)
        }
        runBlocking(Dispatchers.Main) {
            viewModel.deleteNote(note)
        }

           if (!viewModel.listOfNotes.value.contains(note)) {
               val event = runBlocking { viewModel.noteCallBack.value }
               answer = when (event) {
                   NotepadEvent.Empty -> false
                   is NotepadEvent.Failure ->false
                   NotepadEvent.Loading -> false
                   NotepadEvent.Logout -> false
                   is NotepadEvent.Success -> false
                   is NotepadEvent.SuccessAddDelete -> event.message == NOTE_DELETED_LABEL
               }
           }
        assertThat(answer).isTrue()
    }


    @Test
    fun g_AddMultipleNotesAndReceiveListReturnTrue() = runBlockingTest {
        val note_01 = Notes(NOTE_DATE, "My super important note 02", NOTE_IMG_URI, TEST_USER_ID)
        val note_02 = Notes(NOTE_DATE+"1", "My super important note 02", NOTE_IMG_URI, TEST_USER_ID)
        runBlocking(Dispatchers.Main) {
            viewModel.addNote(note_01)
        }
        runBlocking(Dispatchers.Main) {
            viewModel.addNote(note_02)
        }
        val answer = viewModel.listOfNotes.value.contains(note_01) && viewModel.listOfNotes.value.contains(note_02)
        assertThat(answer).isTrue()
    }


    @Test
    fun y_logout() = runBlockingTest {
        var answer = false
        runBlocking(Dispatchers.Main) {
            viewModel.logout()
        }
        val event = runBlocking { viewModel.noteCallBack.value }
        answer = when (event) {
            NotepadEvent.Empty -> false
            is NotepadEvent.Failure -> false
            NotepadEvent.Loading -> false
            NotepadEvent.Logout -> true
            is NotepadEvent.Success -> false
            is NotepadEvent.SuccessAddDelete -> false
        }
        assertThat(answer).isTrue()
    }
}