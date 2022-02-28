package com.example.wealthynotepad.ui.welcomescreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.wealthynotepad.R
import com.example.wealthynotepad.data.Constants
import com.example.wealthynotepad.data.UserEntries
import com.example.wealthynotepad.navigation.Routes
import com.example.wealthynotepad.ui.composes.LoadingCircle
import kotlinx.coroutines.flow.collectLatest


@Composable
fun WelcomeScreen(navController: NavController, viewModel: WelcomeViewModel = hiltViewModel()) {
    val state = viewModel.loginFlow.value
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val scaffoldState = rememberScaffoldState()

    val emailLambda: (String) -> Unit = { it -> email.value = it }
    val passwordLambda: (String) -> Unit = { it -> password.value = it }


    LaunchedEffect(key1 = true) {
        viewModel.snackbarEvent.collectLatest { message ->
            if (message.isNotEmpty()) {
                scaffoldState.snackbarHostState.showSnackbar(
                    message = message
                )
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(text = "Welcome Screen") }) },
        scaffoldState = scaffoldState
    ) {
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
                    .padding(16.dp),
                shape = RoundedCornerShape(10.dp),
                elevation = 5.dp,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(16.dp)
                ) {
                    TopLabel(Constants.LOGIN_REGISTER_LABEL)
                    RegisterData(Constants.EMAIL_LABEL, text = email, emailLambda, state.emailError)
                    RegisterData(
                        Constants.PASSWORD_LABEL,
                        text = password,
                        passwordLambda,
                        state.passwordError
                    )
                    Row(
                        modifier = Modifier
                            .padding(15.dp)
                            .fillMaxWidth(1f),
                        horizontalArrangement = Arrangement.End
                    ) {
                        ButtonLogReg(Constants.LOGIN_LABEL) {
                            viewModel.login(
                                UserEntries(email = email.value, password = password.value)
                            )
                        }
                        Spacer(modifier = Modifier.padding(15.dp))
                        ButtonLogReg(Constants.REGISTRATION_LABEL) {
                            viewModel.register(
                                UserEntries(email = email.value, password = password.value)
                            )
                        }
                    }
                }

            }
        }
        LoadingCircle(state = state.isLoading)
        if (state.userUID.isNotEmpty()) {
            navController.navigate(Routes.Notepad + state.userUID) {
                launchSingleTop = true
            }
        }
    }
}

@Composable
fun TopLabel(text: String) {
    Text(text, fontSize = 24.sp)
}

@Composable
fun RegisterData(
    label: String,
    text: MutableState<String>,
    typeObserver: (String) -> Unit,
    error: Boolean
) {
    val transformation: VisualTransformation =
        if (label == Constants.PASSWORD_LABEL) PasswordVisualTransformation()
        else VisualTransformation.None
    OutlinedTextField(
        modifier = Modifier.padding(vertical = 8.dp),
        value = text.value,
        onValueChange = typeObserver,
        singleLine = true,
        label = { Text(text = label) },
        visualTransformation = transformation,
        isError = error
    )
}

@Composable
fun ButtonLogReg(label: String, onClickHandler: () -> Unit) {
    Button(onClick = onClickHandler) {
        Text(text = label)
    }
}



