package com.example.wealthynotepad



import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.wealthynotepad.navigation.Routes
import com.example.wealthynotepad.ui.notepadscreen.NotepadScreen
import com.example.wealthynotepad.ui.theme.WalthyNotepadTheme
import com.example.wealthynotepad.ui.welcomescreen.WelcomeScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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
                    NavHost(navController = navController, startDestination = Routes.Welcome) {
                        composable(Routes.Welcome) {
                            WelcomeScreen( navController = navController)
                        }
                        composable(
                            Routes.Notepad + "{userUID}",
                            arguments = listOf(
                                navArgument("userUID") { type = NavType.StringType })
                        ) {
                            val userUID = remember { it.arguments?.getString("userUID") }
                            NotepadScreen(
                                userUID = userUID.toString(),
                                navController
                            )
                        }
                    }
                }
            }
        }


    }
}



