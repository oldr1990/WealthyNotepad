package com.example.walthynotepad.notepadscreen


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.util.NotepadEvent
import java.time.LocalDateTime

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotepadScreen(userUID: String, viewModel: NotepadViewModel, navController: NavController) {
    val eventHandler = viewModel.noteCallBack.collectAsState()
    var list by remember { mutableStateOf(listOf(Notes())) }
    eventHandler.value.let {
        when (it) {
            is NotepadEvent.Success -> {
                list = it.notes
            }
            is NotepadEvent.Empty -> {
            }
            is NotepadEvent.SuccessAddDelete -> {

            }
            is NotepadEvent.Failure -> {

            }
            is NotepadEvent.Loading -> {

            }
        }
    }
    val inputText = remember { mutableStateOf("") }
    val inputTextLambda: (String) -> Unit = { it -> inputText.value = it }
    val buttonAddNoteOnClickListener: () -> Unit = {
        val note = Notes(LocalDateTime.now().toString(), inputText.value, "", userUID)
        viewModel.addNote(note)
    }
    Column(verticalArrangement = Arrangement.Bottom) {
        Card(
            elevation = 5.dp, modifier = Modifier
                .fillMaxHeight(0.15f)
                .fillMaxWidth(1f)
                .padding(15.dp,15.dp,15.dp, 0.dp)
                .shadow(5.dp, shape = RoundedCornerShape(5.dp))
        ) {
            Row (verticalAlignment = Alignment.CenterVertically){
                InputData(label = "Type your note here:", text = inputText, inputTextLambda)
                ButtonNotepad(label = "Add note", buttonAddNoteOnClickListener)
            }
        }
        ListOfNotes(list = list)
    }
}

@Composable
fun ButtonNotepad(label: String, onClickHandler: () -> Unit) {
    Button(
        onClick = onClickHandler,
        modifier = Modifier
            .padding(5.dp)
    ) {
        Text(text = label)
    }
}

@Composable
fun InputData(label: String, text: MutableState<String>, typeObserver: (String) -> Unit) {
    OutlinedTextField(
        value = text.value,
        onValueChange = typeObserver,
        singleLine = true,
        label = { Text(text = label) },
        modifier = Modifier.padding(5.dp).width(IntrinsicSize.Min)
    )
}

@Composable
fun ListOfNotes(list: List<Notes>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(15.dp,0.dp,15.dp,15.dp)
    ) {
        items(list) {
            NoteCardView(it)
        }
    }
}

@Composable
fun NoteCardView(it: Notes) {
    Card(
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(5.dp),
        elevation = 5.dp
    ) {
        Box(modifier = Modifier.padding(15.dp)) {
            Column {
                Text(text = it.date)
                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = it.text)
            }
        }
    }
}
