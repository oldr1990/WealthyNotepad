package com.example.wealthynotepad.ui.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.wealthynotepad.ui.theme.HalfTransparent

@Preview
@Composable
fun PreviewLoadingCircle() {
    val state = remember { mutableStateOf(true) }
    Box(modifier = Modifier.width(200.dp).height(200.dp).background(Color.Cyan), ) {
        LoadingCircle(state = state)
    }
}

@Composable
fun LoadingCircle(state: MutableState<Boolean>) {
    if (state.value) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .background(HalfTransparent)
                .clickable { }
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
    }
}