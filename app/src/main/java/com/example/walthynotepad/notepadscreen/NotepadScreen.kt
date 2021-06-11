package com.example.walthynotepad.notepadscreen

import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.example.walthynotepad.welcomescreen.Label

@Composable
fun NotepadScreen(userUID: String){
    Text(text = userUID)
}

@Composable
fun ButtonNotepad(label: String, onClickHandler: () -> Unit) {
    Button(onClick = onClickHandler) {
        Text(text = label)
    }
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
