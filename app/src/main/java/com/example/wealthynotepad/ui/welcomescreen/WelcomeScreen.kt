package com.example.wealthynotepad.ui.welcomescreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.wealthynotepad.R
import com.example.wealthynotepad.data.Constants
import com.example.wealthynotepad.data.UserEntries
import com.example.wealthynotepad.ui.composes.LoadingCircle
import com.example.wealthynotepad.util.isItEmail
import com.google.accompanist.coil.rememberCoilPainter


@Composable
fun WelcomeScreen(viewModel: WelcomeVIewModel, navController: NavController) {
    val eventHandler = viewModel.loginFlow.collectAsState()
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val loading = rememberSaveable { mutableStateOf(false) }

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
        }
    }
    val loginClickListener: () -> Unit = {
        if (isItEmail(email.value)) {
            if (password.value.length in 4..10) {
                viewModel.login(
                    UserEntries(
                        email = email.value,
                        password = password.value,
                    )
                )
            }
        } else {
            Log.e("!@#", Constants.ERROR_INVALID_EMAIL)
        }
    }

    eventHandler.value.let { response ->
        when (response) {
            is LoginEvent.Success -> {
                loading.value = false
                navController.navigate("notepad_screen/${response.uid}")
            }
            is LoginEvent.Empty -> {
                loading.value = false
            }
            is LoginEvent.Loading -> {
                loading.value = true
            }
            is LoginEvent.Failure -> {
                Toast.makeText(LocalContext.current, response.errorText, Toast.LENGTH_SHORT).show()
                loading.value = false
            }
        }
    }

    Box {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize(1f)
        ) {
            Image(
                painter = painterResource(id = R.drawable.wealthy_notepad_icon),
                contentDescription = "Icon"
            )
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
                    TopLabel(Constants.LOGIN_REGISTER_LABEL)
                    RegisterData(Constants.EMAIL_LABEL, text = email, emailLambda)
                    RegisterData(Constants.PASSWORD_LABEL, text = password, passwordLambda)
                    Row(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ButtonLogReg(label = Constants.LOGIN_LABEL, loginClickListener)
                        Spacer(modifier = Modifier.padding(15.dp))
                        ButtonLogReg(label = Constants.REGISTRATION_LABEL, registerClickListener)
                    }
                }

            }
        }
        LoadingCircle(state = loading)
    }
}

@Composable
fun TopLabel(text: String) {
    Text(text, fontSize = 24.sp)
}

@Composable
fun RegisterData(label: String, text: MutableState<String>, typeObserver: (String) -> Unit) {
    val transformation: VisualTransformation =
        if (label == Constants.PASSWORD_LABEL) PasswordVisualTransformation()
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



