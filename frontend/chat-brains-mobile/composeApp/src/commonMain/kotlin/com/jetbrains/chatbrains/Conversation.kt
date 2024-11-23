package com.jetbrains.chatbrains

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable

@Composable
fun Conversation(onNavigate: () -> Unit) {
    Button(onClick = onNavigate) {
        Text("Go to Login")
    }
}