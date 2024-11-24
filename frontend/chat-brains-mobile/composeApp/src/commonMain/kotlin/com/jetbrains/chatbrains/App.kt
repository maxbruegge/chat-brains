package com.jetbrains.chatbrains

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import chat_brains_mobile.composeapp.generated.resources.Res
import chat_brains_mobile.composeapp.generated.resources.compose_multiplatform
import com.jetbrains.chatbrains.networking.NetworkClient
import dev.theolm.record.Record
import dev.theolm.record.config.OutputFormat
import dev.theolm.record.config.OutputLocation
import dev.theolm.record.config.RecordConfig
import kotlinx.coroutines.launch
import util.NetworkError
import util.onError
import util.onSuccess

import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color


sealed class Screen {
    object LoginScreen : Screen()
    object BranchesScreen : Screen()
    object ConversationScreen : Screen()
}

@Composable
@Preview
fun App(client: NetworkClient) {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.LoginScreen) }

    Box(
        modifier = Modifier
            .fillMaxSize() // Ensures the background covers the entire screen
            .background(Color(0xFF1E1F22)) // Replace with your desired color
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(WindowInsets.systemBars.asPaddingValues()) // Adjust content for safe area
        ) {
            when (currentScreen) {
                is Screen.LoginScreen -> Login(
                    client = client,
                    onNavigate = { currentScreen = Screen.BranchesScreen }
                )
                is Screen.BranchesScreen -> Branches(
                    client = client,
                    onNavigate = { currentScreen = Screen.ConversationScreen }
                )
                is Screen.ConversationScreen -> Conversation(
                    client = client,
                    onNavigate = { }
                )
            }
        }
    }

}