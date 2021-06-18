package com.example.walthynotepad.ui.composes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.walthynotepad.ui.theme.HalfTransparent


@Composable
fun LoadingCircle(state: MutableState<Boolean>) {
    if (state.value) {
        Box(
            contentAlignment = Alignment.Center, modifier = Modifier
                .fillMaxWidth(1f)
                .fillMaxHeight(1f)
                .background(HalfTransparent)
                .clickable {  }
        ) {
            CircularProgressIndicator(color = MaterialTheme.colors.primary)
        }
    }
}