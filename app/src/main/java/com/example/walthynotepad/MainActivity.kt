package com.example.walthynotepad



import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.example.walthynotepad.data.Constants.BACK_PRESSED_MESSAGE
import com.example.walthynotepad.ui.notepadscreen.NotepadScreen
import com.example.walthynotepad.ui.notepadscreen.NotepadViewModel
import com.example.walthynotepad.ui.theme.WalthyNotepadTheme
import com.example.walthynotepad.ui.welcomescreen.WelcomeScreen
import com.example.walthynotepad.ui.welcomescreen.WelcomeVIewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlin.system.exitProcess

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel: WelcomeVIewModel by viewModels()
    private val noteViewModel: NotepadViewModel by viewModels()
    private var backPressedTimer = 0L

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backPressedTimer < 2000)
        exitProcess(-1)
        else {
            backPressedTimer = System.currentTimeMillis()
            Toast.makeText(this, BACK_PRESSED_MESSAGE,Toast.LENGTH_SHORT).show()
        }
    }


    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_WalthyNotepad)
        setContent {
            val navController = rememberNavController()
            WalthyNotepadTheme {
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize(1f)
                ) {
                    NavHost(navController = navController, startDestination = "welcome_screen") {
                        composable("welcome_screen") {
                            WelcomeScreen(viewModel, navController = navController)
                        }
                        composable(
                            "notepad_screen/{userUID}",
                            arguments = listOf(navArgument("userUID") { type = NavType.StringType })
                        ) {
                            val userUID = remember { it.arguments?.getString("userUID") }
                            NotepadScreen(
                                userUID = userUID.toString(),
                                noteViewModel,
                                navController
                            )
                        }
                    }
                }
            }
        }


    }
}



