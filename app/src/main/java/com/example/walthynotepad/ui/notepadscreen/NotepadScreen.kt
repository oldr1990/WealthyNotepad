package com.example.walthynotepad.ui.notepadscreen


import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.walthynotepad.data.Constants
import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.ui.composes.LoadingCircle
import com.example.walthynotepad.util.NotepadEvent
import com.google.accompanist.coil.rememberCoilPainter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Preview
@Composable
fun Preview() {
    val url: MutableState<Uri> =
        remember { mutableStateOf("https://firebasestorage.googleapis.com/v0/b/fir-notebook-24c3a.appspot.com/o/image%2F-518351845?alt=media&token=0a8208b8-72a1-49d7-968b-6fd050fb4694".toUri()) }
    val inputText = remember { mutableStateOf(Constants.emptyString) }
    EditorCard(
        inputText = inputText,
        inputTextLambda = { },
        buttonAddNoteOnClickListener = { },
        addImageOnClickListener = { },
        imgUri = url
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotepadScreen(userUID: String, viewModel: NotepadViewModel, navController: NavController) {
    val imageUri = remember { mutableStateOf(Uri.EMPTY) }
    val eventHandler = viewModel.noteCallBack.collectAsState()
    var list by remember { mutableStateOf(listOf(Notes())) }
    val inputText = remember { mutableStateOf(Constants.emptyString) }
    val loadingState = remember { mutableStateOf(false) }

    val inputTextLambda: (String) -> Unit = { it -> inputText.value = it }
    val buttonAddNoteOnClickListener: () -> Unit = {
        val dateTime = LocalDateTime.now()
        val formattedDateTime: String =
            dateTime.format(DateTimeFormatter.ofPattern(Constants.dataFormatPattern))
        val note = Notes(formattedDateTime, inputText.value, imageUri.value.toString(), userUID)
        viewModel.addNote(note)
    }
    val noteDeleteOnClickListener: (Notes) -> Unit = {
        viewModel.deleteNote(it)
    }
    val imageReferenceResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            imageUri.value = it
        }
    val addImageOnClickListener: () -> Unit =
        { imageReferenceResult.launch(Constants.imageSearchType) }

    eventHandler.value.let {
        list = viewModel.listOfNotes.value
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

    Box(contentAlignment = BottomCenter) {
        LazyColumn(
            reverseLayout = true,
            modifier = Modifier
                .fillMaxWidth(1f)
                .padding(15.dp, 0.dp, 15.dp, 15.dp)
        ) {
            item {
                EditorCard(
                    inputText = inputText,
                    inputTextLambda = inputTextLambda,
                    buttonAddNoteOnClickListener = buttonAddNoteOnClickListener,
                    addImageOnClickListener = addImageOnClickListener,
                    imgUri = imageUri
                )
            }
            items(list) {
                NoteCardView(it, noteDeleteOnClickListener)
            }
        }
    }
    LoadingCircle(state = loadingState)
}

@Composable
fun EditorCard(
    inputText: MutableState<String>,
    inputTextLambda: (String) -> Unit,
    buttonAddNoteOnClickListener: () -> Unit,
    addImageOnClickListener: () -> Unit,
    imgUri: MutableState<Uri>
) {
    val photoButtonLabel = remember { mutableStateOf(Constants.choseYourImage) }
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
            if (imgUri.value != Uri.EMPTY) {
                photoButtonLabel.value = Constants.changeYourImage
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = CenterVertically,
                    modifier = Modifier.fillMaxWidth(1f),
                ) {

                    Card(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(5),
                        modifier = Modifier
                            .width(300.dp)
                            .height(300.dp)
                            .padding(10.dp, 15.dp, 10.dp, 10.dp)
                    ) {
                        Box(
                            contentAlignment = TopEnd,
                            modifier = Modifier
                                .fillMaxHeight(1f)
                                .fillMaxWidth(1f)
                        ) {

                            Image(
                                painter = rememberCoilPainter(request = imgUri.value),
                                contentDescription = Constants.yourImage,
                                modifier = Modifier
                                    .fillMaxWidth(1f)
                                    .fillMaxHeight(1f),
                                contentScale = ContentScale.Crop,
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize(1f)
                                    .background(
                                        brush = Brush.linearGradient(
                                            listOf(Color.Black, Color.Transparent),
                                            start = Offset(900f, 0f),
                                            end = Offset(800f, 200f)
                                        )
                                    )
                            )
                            Icon(
                                imageVector = Icons.Default.DeleteForever,
                                tint = Color.White,
                                contentDescription = "",
                                modifier = Modifier
                                    .padding(15.dp)
                                    .clickable {
                                        imgUri.value = Uri.EMPTY
                                    })
                        }
                    }

                }
            } else photoButtonLabel.value = Constants.choseYourImage
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(CenterVertically)
            ) {
                InputData(
                    label = Constants.enterYourNote,
                    text = inputText,
                    inputTextLambda
                )
            }
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(CenterVertically)
                    .padding(5.dp)
            ) {
                ButtonNotepad(label = photoButtonLabel.value, addImageOnClickListener)
                ButtonNotepad(label = Constants.addNoteLabel, buttonAddNoteOnClickListener)
            }
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

@Composable
fun NoteCardViewWithImage(it: Notes, deleteLambda: (Notes) -> Unit) {
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

