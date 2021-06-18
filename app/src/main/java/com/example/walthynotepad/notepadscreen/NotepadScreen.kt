package com.example.walthynotepad.notepadscreen


import android.os.Build
import android.widget.Toast
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
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
    LazyColumn(
        reverseLayout = true,
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(15.dp, 0.dp, 15.dp, 15.dp)
    ) {
        item {
            Card(
                elevation = 5.dp, modifier = Modifier
                    .wrapContentHeight(CenterVertically)
                    .fillMaxWidth(1f)
                    .padding(5.dp, 10.dp, 5.dp, 5.dp)
                    .shadow(5.dp, shape = RoundedCornerShape(5.dp))
            ) {
                Column(verticalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .wrapContentHeight(CenterVertically)
                )
                {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .wrapContentHeight(CenterVertically)
                    ) {
                        InputData(label = "Type your note here:", text = inputText, inputTextLambda)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .wrapContentHeight(CenterVertically)
                            .padding(5.dp)
                    ) {
                        ButtonNotepad(label = "Add note", buttonAddNoteOnClickListener)
                    }
                }
            }
        }
        items(list) {
            NoteCardView(it)
        }
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
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(10.dp,5.dp,10.dp,5.dp)
            .width(IntrinsicSize.Min)
    )
}

@Composable
fun ListOfNotes(list: List<Notes>) {
    LazyColumn(
        reverseLayout = true,
        modifier = Modifier
            .fillMaxWidth(1f)
            .padding(15.dp, 0.dp, 15.dp, 15.dp)
    ) {
        item {

        }
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
