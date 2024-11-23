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

@Composable
@Preview
fun App(client: NetworkClient) {
    MaterialTheme {
        var isRecording by remember { mutableStateOf(false) }
        var isLoading by remember {
            mutableStateOf(false)
        }
        var errorMessage by remember {
            mutableStateOf<NetworkError?>(null)
        }
        val scope = rememberCoroutineScope()

        Column(Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Button(onClick = {
                if (isRecording) {
                    Record.stopRecording().also { savedAudioPath ->
                        println("Recording stopped. File saved at $savedAudioPath")
                    }
                } else {
                    Record.startRecording()
                }
                isRecording = !isRecording
            }) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            Button(onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null

                    client.login("fuck you", "lol")
                        .onSuccess {
                            errorMessage = null
                        }
                        .onError {
                            errorMessage = it
                        }
                    isLoading = false
                }
            }) {
                Text("Send Request")
            }

            errorMessage?.let {
                Text(
                    text = it.name
                )
            }
        }
    }


}