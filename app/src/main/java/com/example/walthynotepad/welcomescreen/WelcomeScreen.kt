package com.example.walthynotepad.welcomescreen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walthynotepad.data.UserEntries
import com.example.walthynotepad.util.LoginEvent

import java.util.regex.Pattern


object Label {
    const val emailLabel: String = "Email"
    const val passwordLabel: String = "Password"
    const val login = "LogIn / Register"
    const val loginButton = "LogIn"
    const val registration = "Registration"
    const val emailInvalid = "Email is invalid! Please type correct email."
}


@Composable
fun WelcomeScreen(viewModel: WelcomeVIewModel, navController: NavController) {
    val eventHandler = viewModel.loginFlow.collectAsState()
    eventHandler.value.let {
        when (it) {
            is LoginEvent.Success -> {
                navController.navigate("notepad_screen/${it.resultText}")
            }
            is LoginEvent.Empty -> {
                Log.e("!@#", "Empty!")
            }
            is LoginEvent.Loading -> {
                Log.e("!@#", "Loading!")
            }
            is LoginEvent.Failure -> {
                Log.e("!@#", "Failure! ${it.errorText}")
            }
        }
    }

    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }

    val emailLambda: (String) -> Unit = { it -> email.value = it }
    val passwordLambda: (String) -> Unit = { it -> password.value = it }

    val registerClickListener: () -> Unit = {
        if (isItEmail(email.value)) {
            if (password.value.length in 4..10) {
                viewModel.register(
                    UserEntries(
                        email = email.value,
                        password = password.value,
                    )
                )
            }
        } else {
            Log.e("!@#", Label.emailInvalid)
        }
    }
    val loginClickListener: () -> Unit = {
        Log.e("!@#", "Login* Email: ${email.value}  Password: ${password.value}")
    }
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(15.dp),
            shape = RoundedCornerShape(10.dp),
            elevation = 5.dp,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(15.dp)
            ) {
                TopLabel(Label.login)
                RegisterData(Label.emailLabel, text = email, emailLambda)
                RegisterData(Label.passwordLabel, text = password, passwordLambda)
                Row(
                    modifier = Modifier
                        .padding(15.dp)
                        .fillMaxWidth(1f),
                    horizontalArrangement = Arrangement.End
                ) {
                    ButtonLogReg(label = Label.loginButton, loginClickListener)
                    Spacer(modifier = Modifier.padding(15.dp))
                    ButtonLogReg(label = Label.registration, registerClickListener)
                }
            }

        }
    }
}

@Composable
fun TopLabel(text: String) {
    Text(text, fontSize = 24.sp)
}

@Composable
fun RegisterData(label: String, text: MutableState<String>, typeObserver: (String) -> Unit) {
    val transformation: VisualTransformation =
        if (label == Label.passwordLabel) PasswordVisualTransformation()
        else VisualTransformation.None
    OutlinedTextField(
        value = text.value,
        onValueChange = typeObserver,
        singleLine = true,
        label = { Text(text = label) },
        visualTransformation = transformation,

        )
}

@Composable
fun ButtonLogReg(label: String, onClickHandler: () -> Unit) {
    Button(onClick = onClickHandler) {
        Text(text = label)
    }
}

fun isItEmail(toCheck: String): Boolean {
    val emailCheckPattern = Pattern.compile(
        "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}" +
                "\\@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )
    return emailCheckPattern.matcher(toCheck).matches()
}
