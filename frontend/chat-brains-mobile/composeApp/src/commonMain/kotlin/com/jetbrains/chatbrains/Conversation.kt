package com.jetbrains.chatbrains

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1F22)) // Set the background color
        ) {
            // Content Column (Responses, Errors, Loading)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 96.dp), // Reserve space for the button (ensure enough height)
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Loading Indicator
                if (isLoading) {
                    Text(
                        "Processing...",
                        style = MaterialTheme.typography.body1,
                        color = Color.White
                    )
                }

                // Error Message
                errorMessage?.let {
                    Text(
                        text = it.name,
                        color = MaterialTheme.colors.error
                    )
                }

                // Display the list of responses
                responses.forEach { response ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF2B2D30))
                    ) {
                        Text(
                            text = response,
                            style = MaterialTheme.typography.body1.copy(
                                color = Color.White
                            ),
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }

            // Recording Button at the Bottom
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (isRecording) {
                            Record.stopRecording().also { savedAudioPath ->
                                println("Recording stopped. File saved at $savedAudioPath")
                                scope.launch {
                                    isLoading = true
                                    errorMessage = null

                                    client.answer(filePath = savedAudioPath)
                                        .onSuccess { response ->
                                            errorMessage = null
                                            responses = responses + response
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
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp) // Ensure button height is fixed
                        .clip(RoundedCornerShape(16.dp)), // Rounded corners
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = Color(0xFF377287), // Custom background color
                        contentColor = Color.White // Text color
                    )
                ) {
                    Text(if (isRecording) "Stop Recording" else "Start Recording")
                }
            }
        }
    }
}

