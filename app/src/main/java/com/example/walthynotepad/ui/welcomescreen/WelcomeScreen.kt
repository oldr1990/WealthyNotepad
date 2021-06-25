package com.example.walthynotepad.ui.welcomescreen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.walthynotepad.data.Constants
import com.example.walthynotepad.data.UserEntries
import com.example.walthynotepad.ui.composes.LoadingCircle
import com.example.walthynotepad.util.LoginEvent

import java.util.regex.Pattern


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
        } else {
            Log.e("!@#", Constants.ERROR_INVALID_EMAIL)
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
    
    eventHandler.value.let {
        when (it) {
            is LoginEvent.Success -> {
                loading.value = false
                navController.navigate("notepad_screen/${it.uid}")
            }
            is LoginEvent.Empty -> {
                loading.value = false
                Log.e("!@#", "Empty!")
            }
            is LoginEvent.Loading -> {
             loading.value = true

                Log.e("!@#", "Loading!")
            }
            is LoginEvent.Failure -> {
                Toast.makeText(LocalContext.current, it.errorText, Toast.LENGTH_SHORT).show()
                loading.value = false
            }
        }
    }

  Box {
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


