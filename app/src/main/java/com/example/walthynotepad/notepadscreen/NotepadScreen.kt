package com.example.walthynotepad.notepadscreen


import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.walthynotepad.data.Constants
import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.ui.composes.LoadingCircle
import com.example.walthynotepad.util.NotepadEvent
import com.google.type.DateTime
import okhttp3.internal.format
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotepadScreen(userUID: String, viewModel: NotepadViewModel, navController: NavController) {
    val eventHandler = viewModel.noteCallBack.collectAsState()
    var list by remember { mutableStateOf(listOf(Notes())) }
    val inputText = remember { mutableStateOf("") }
    val loadingState = remember { mutableStateOf(false) }
    val inputTextLambda: (String) -> Unit = { it -> inputText.value = it }
    val buttonAddNoteOnClickListener: () -> Unit = {
        val dateTime = LocalDateTime.now()
        val formattedDateTime: String =
            dateTime.format(DateTimeFormatter.ofPattern(Constants.dataFormatPattern))
        val note = Notes(formattedDateTime, inputText.value, Constants.emptyString, userUID)
        viewModel.addNote(note)
    }
    val noteDeleteOnClickListener: (Notes) -> Unit = {
        viewModel.deleteNote(it)
    }

    eventHandler.value.let {
        when (it) {
            is NotepadEvent.Success -> {
                loadingState.value = false
                list = it.notes
                inputText.value = Constants.emptyString
            }
            is NotepadEvent.Empty -> {
                loadingState.value = false
            }
            is NotepadEvent.SuccessAddDelete -> {
                loadingState.value = false
                Toast.makeText(LocalContext.current, it.message, Toast.LENGTH_SHORT).show()
            }
            is NotepadEvent.Failure -> {
                loadingState.value = false
                Toast.makeText(LocalContext.current, it.message, Toast.LENGTH_SHORT).show()
            }
            is NotepadEvent.Loading -> {
                loadingState.value = true
            }
        }
    }

    Box {
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
                    Column(
                        verticalArrangement = Arrangement.SpaceEvenly,
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
                            InputData(
                                label = "Type your note here:",
                                text = inputText,
                                inputTextLambda
                            )
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
                NoteCardView(it, noteDeleteOnClickListener)
            }
        }
    }
    LoadingCircle(state = loadingState)
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
            .padding(10.dp, 5.dp, 10.dp, 5.dp)
            .width(IntrinsicSize.Min)
    )
}


@Composable
fun NoteCardView(it: Notes, deleteLambda: (Notes) -> Unit) {
    if (it.date != Constants.emptyString && it.text != Constants.emptyString) {
        Card(
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(5.dp),
            elevation = 5.dp
        ) {

            Column(
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(CenterVertically)
                    .padding(15.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(1f)
                ) {

                    Text(text = it.date)
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { deleteLambda(it) }
                    )
                }

                Spacer(modifier = Modifier.padding(5.dp))
                Text(text = it.text)
            }


        }
    }
}
