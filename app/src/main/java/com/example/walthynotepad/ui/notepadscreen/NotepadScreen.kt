package com.example.walthynotepad.ui.notepadscreen


import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Alignment.Companion.TopEnd
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.example.walthynotepad.data.Constants
import com.example.walthynotepad.data.Constants.DATE_FORMAT_PATTERN
import com.example.walthynotepad.data.Constants.NAVIGATION_WELCOME_SCREEN
import com.example.walthynotepad.data.Notes
import com.example.walthynotepad.ui.composes.LoadingCircle
import com.example.walthynotepad.util.NotepadEvent
import com.example.walthynotepad.util.millisToDate
import com.google.accompanist.coil.rememberCoilPainter
import com.google.accompanist.imageloading.ImageLoadState
import com.example.walthynotepad.ui.notepadscreen.EditorCard as EditorCard1


@Preview
@Composable
fun Preview() {
    val url: MutableState<Uri> =
        remember { mutableStateOf("https://firebasestorage.googleapis.com/v0/b/fir-notebook-24c3a.appspot.com/o/image%2F-518351845?alt=media&token=0a8208b8-72a1-49d7-968b-6fd050fb4694".toUri()) }
    val inputText = remember { mutableStateOf(Constants.EMPTY_STRING) }
    EditorCard1(
        inputText = inputText,
        inputTextLambda = { },
        buttonAddNoteOnClickListener = { },
        addImageOnClickListener = { },
        {},
        imgUri = url
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NotepadScreen(userUID: String, viewModel: NotepadViewModel, navController: NavController) {
    val imageUri = remember { mutableStateOf(Uri.EMPTY) }
    val eventHandler = viewModel.noteCallBack.collectAsState()
    var list by remember { mutableStateOf(listOf(Notes())) }
    val inputText = remember { mutableStateOf(Constants.EMPTY_STRING) }
    val loadingState = remember { mutableStateOf(false) }

    val logoutOnClickListener: () -> Unit = {viewModel.logout() }
    val inputTextLambda: (String) -> Unit = { it -> inputText.value = it }
    val buttonAddNoteOnClickListener: () -> Unit = {
        if (inputText.value != Constants.EMPTY_STRING) {
            val formattedDateTime = System.currentTimeMillis().toString()
            val note = Notes(formattedDateTime, inputText.value, imageUri.value.toString(), userUID)
            viewModel.addNote(note)
        }
    }
    val noteDeleteOnClickListener: (Notes) -> Unit = {
        viewModel.deleteNote(it)
    }
    val imageReferenceResult =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) {
            imageUri.value = it
        }
    val addImageOnClickListener: () -> Unit =
        { imageReferenceResult.launch(Constants.IMAGE_SEARCH_TYPE) }

    eventHandler.value.let {
        list = viewModel.listOfNotes.value
        when (it) {
            is NotepadEvent.Success -> {
                loadingState.value = false
                list = it.notes
                inputText.value = Constants.EMPTY_STRING
                imageUri.value = Uri.EMPTY
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
            NotepadEvent.Logout -> {
                navController.navigate(NAVIGATION_WELCOME_SCREEN)
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
                EditorCard1(
                    inputText = inputText,
                    inputTextLambda = inputTextLambda,
                    buttonAddNoteOnClickListener = buttonAddNoteOnClickListener,
                    addImageOnClickListener = addImageOnClickListener,
                    logoutOnClickListener = logoutOnClickListener,
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
    logoutOnClickListener: ()-> Unit,
    imgUri: MutableState<Uri>
) {
    val photoButtonLabel = remember { mutableStateOf(Constants.CHOSE_YOUR_IMAGE_LABEL) }
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
                horizontalArrangement = Arrangement.End,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(CenterVertically)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Logout",
                    tint = Color.Red,
                    modifier = Modifier.clickable {
                    logoutOnClickListener()
                    }
                )
            }
            if (imgUri.value != Uri.EMPTY) {
                photoButtonLabel.value = Constants.CHANGE_YOUR_IMAGE_LABEL
                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = CenterVertically,
                    modifier = Modifier.fillMaxWidth(1f),
                ) {

                    Card(
                        elevation = 5.dp,
                        shape = RoundedCornerShape(5),
                        modifier = Modifier
                            .size(300.dp)
                            .padding(10.dp)
                    ) {
                        Box(
                            contentAlignment = TopEnd,
                            modifier = Modifier
                                .fillMaxHeight(1f)
                                .fillMaxWidth(1f)
                        ) {

                            Image(
                                painter = rememberCoilPainter(request = imgUri.value),
                                contentDescription = Constants.YOUR_IMAGE_LABEL,
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
            } else photoButtonLabel.value = Constants.CHOSE_YOUR_IMAGE_LABEL
            Row(
                verticalAlignment = CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth(1f)
                    .wrapContentHeight(CenterVertically)
            ) {
                InputData(
                    label = Constants.ENTER_YOUR_NOTE_LABEL,
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
                ButtonNotepad(label = Constants.ADD_NOTE_LABEL, buttonAddNoteOnClickListener)
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
fun NoteCardView(note: Notes, deleteLambda: (Notes) -> Unit) {
    if (note.date != Constants.EMPTY_STRING && note.text != Constants.EMPTY_STRING) {
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

                    Text(text = note.date.millisToDate(DATE_FORMAT_PATTERN))
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier
                            .size(24.dp)
                            .clickable { deleteLambda(note) }
                    )
                }
                if (note.img == Constants.EMPTY_STRING) {
                    Spacer(modifier = Modifier.padding(10.dp))
                } else Card(
                    shape = RoundedCornerShape(10.dp),
                    elevation = 5.dp,
                    modifier = Modifier
                        .padding(10.dp)
                        .size(300.dp)
                ) {
                    val painter = rememberCoilPainter(request = note.img)
                    Image(
                        painter = painter,
                        contentDescription = Constants.YOUR_IMAGE_LABEL,
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .fillMaxHeight(1f),
                        contentScale = ContentScale.Crop,
                    )
                    when (painter.loadState) {
                        is ImageLoadState.Loading -> {
                            Box(modifier = Modifier.size(50.dp), contentAlignment = Center) {
                                CircularProgressIndicator(modifier = Modifier.size(50.dp))
                            }
                        }
                        ImageLoadState.Empty -> {}
                        is ImageLoadState.Success -> {}
                        is ImageLoadState.Error -> {}
                    }
                }
                Text(text = note.text)
            }


        }
    }
}

