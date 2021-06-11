package com.example.walthynotepad.notepadscreen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.util.NotepadEvent

@Composable
fun NotepadScreen(userUID: String, viewModel: NotepadViewModel, navController: NavController) {
    val eventHandler = viewModel.noteCallBack.collectAsState()
    eventHandler.value.let {
        when(it){
            is NotepadEvent.Success ->{
                Text(text = it.toString())}
            is NotepadEvent.Empty ->{}
            is NotepadEvent.SuccessAddDelete ->{
                Text(text = it.message)}
            is NotepadEvent.Failure ->{
                Text(text = it.message)}
            is NotepadEvent.Loading ->{}
        }
    }
    val inputText = remember{ mutableStateOf("")}
    val inputTextLambda: (String) -> Unit = {it -> inputText.value = it}
    val buttonAddNoteOnClickListener: () -> Unit = {
        val note = Notes("",inputText.value,"",userUID)
        viewModel.addNote(note)
    }
    Column(verticalArrangement = Arrangement.Center) {
        PagingView()
        Card(
            elevation = 5.dp, modifier = Modifier
                .fillMaxHeight(0.15f)
                .fillMaxWidth(1f)
                .padding(15.dp)
                .shadow(5.dp, shape = RoundedCornerShape(5.dp))
        ) {
            Row(horizontalArrangement = Arrangement.Center) {
                InputData(label = "Type your note here:", text = inputText, inputTextLambda)
                ButtonNotepad(label = "Add note", buttonAddNoteOnClickListener)
            }
        }
    }
}

@Composable
fun PagingView() {
    Text(text = "PagingView")
}

@Composable
fun ButtonNotepad(label: String, onClickHandler: () -> Unit) {
    Button(onClick = onClickHandler) {
        Text(text = label)
    }
}

@Composable
fun InputData(label: String, text: MutableState<String>, typeObserver: (String) -> Unit) {
    OutlinedTextField(
        value = text.value,
        onValueChange = typeObserver,
        singleLine = true,
        label = { Text(text = label) }
        )
}
