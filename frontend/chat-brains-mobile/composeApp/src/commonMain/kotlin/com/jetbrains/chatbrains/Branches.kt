package com.jetbrains.chatbrains

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.jetbrains.chatbrains.networking.NetworkClient

@Composable
fun Branches(client: NetworkClient, onNavigate: () -> Unit) {
    Button(onClick = onNavigate) {
        Text("Select Branch")
    }
}