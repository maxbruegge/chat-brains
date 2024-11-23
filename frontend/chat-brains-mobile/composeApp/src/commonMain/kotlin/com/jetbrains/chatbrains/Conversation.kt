package com.jetbrains.chatbrains

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.jetbrains.chatbrains.networking.NetworkClient
import dev.theolm.record.Record
import kotlinx.coroutines.launch
import util.NetworkError
import util.onError
import util.onSuccess

@Composable
fun Conversation(client: NetworkClient, onNavigate: () -> Unit) {
    MaterialTheme {
        var isRecording by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }
        var errorMessage by remember { mutableStateOf<NetworkError?>(null) }
        var responses by remember { mutableStateOf<List<String>>(emptyList()) }

        val scope = rememberCoroutineScope()

        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.Bottom)
        ) {
            // Start/Stop Recording Button
            Button(onClick = {
                if (isRecording) {
                    Record.stopRecording().also { savedAudioPath ->
                        println("Recording stopped. File saved at $savedAudioPath")
                        scope.launch {
                            isLoading = true
                            errorMessage = null

                            client.answer(filePath = savedAudioPath)
                                .onSuccess { response ->
                                    errorMessage = null
                                    responses = responses + response // Add the response to the list
                                }
                                .onError {
                                    errorMessage = it
                                }
                            isLoading = false
                        }
                    }
                } else {
                    Record.startRecording()
                }
                isRecording = !isRecording
            }) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            // Loading Indicator
            if (isLoading) {
                Text("Processing...", style = MaterialTheme.typography.body1)
            }

            // Error Message
            errorMessage?.let {
                Text(
                    text = it.name,
                    color = MaterialTheme.colors.error
                )
            }

            // Display the list of responses
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                responses.forEach { response ->
                    Text(
                        text = response,
                        style = MaterialTheme.typography.body1,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}