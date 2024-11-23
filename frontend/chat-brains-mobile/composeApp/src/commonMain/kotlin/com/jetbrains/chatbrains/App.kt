package com.jetbrains.chatbrains

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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


sealed class Screen {
    object LoginScreen : Screen()
    object BranchesScreen : Screen()
    object ConversationScreen : Screen()
}

@Composable
@Preview
fun App(client: NetworkClient) {

    var currentScreen by remember { mutableStateOf<Screen>(Screen.LoginScreen) }

    when (currentScreen) {
        is Screen.LoginScreen -> Login(
            client = client,
            onNavigate = { currentScreen = Screen.BranchesScreen }
        )
        is Screen.BranchesScreen -> Branches(
            client = client,
            onNavigate = { currentScreen = Screen.ConversationScreen }
        )
        is Screen.ConversationScreen -> Conversation(onNavigate = { currentScreen = Screen.LoginScreen })
    }


//    MaterialTheme {
//        var isRecording by remember { mutableStateOf(false) }
//        var isLoading by remember {
//            mutableStateOf(false)
//        }
//        var errorMessage by remember {
//            mutableStateOf<NetworkError?>(null)
//        }
//        val scope = rememberCoroutineScope()
//
//        Column(Modifier.fillMaxSize(),
//            horizontalAlignment = Alignment.CenterHorizontally,
//            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
//        ) {
//            Button(onClick = {
//                if (isRecording) {
//                    Record.stopRecording().also { savedAudioPath ->
//                        println("Recording stopped. File saved at $savedAudioPath")
//                        scope.launch {
//                            isLoading = true
//                            errorMessage = null
//
//                            client.answer(filePath = savedAudioPath)
//                                .onSuccess {
//                                    errorMessage = null
//                                }
//                                .onError {
//                                    errorMessage = it
//                                }
//                            isLoading = false
//                        }
//                    }
//                } else {
//                    Record.startRecording()
//                }
//                isRecording = !isRecording
//            }) {
//                Text(if (isRecording) "Stop Recording" else "Start Recording")
//            }
//
//            Button(onClick = {
//                scope.launch {
//                    isLoading = true
//                    errorMessage = null
//
//                    client.login("example@jetbrains.de", "password")
//                        .onSuccess {
//                            errorMessage = null
//                        }
//                        .onError {
//                            errorMessage = it
//                        }
//                    isLoading = false
//                }
//            }) {
//                Text("Sign In")
//            }
//
//            Button(onClick = {
//                scope.launch {
//                    isLoading = true
//                    errorMessage = null
//
//                    client.conversations()
//                        .onSuccess {
//                            errorMessage = null
//                        }
//                        .onError {
//                            errorMessage = it
//                        }
//                    isLoading = false
//                }
//            }) {
//                Text("Get Conversations")
//            }
//
//
//            errorMessage?.let {
//                Text(
//                    text = it.name
//                )
//            }
//        }
//    }
}