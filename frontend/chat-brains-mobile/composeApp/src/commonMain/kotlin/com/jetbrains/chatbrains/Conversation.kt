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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1F22)), // Set the background color
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
                    .fillMaxWidth() // Full width
                    .height(64.dp) // Set desired height
                    .clip(RoundedCornerShape(16.dp)), // Rounded corners
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFF377287), // Custom background color
                    contentColor = Color.White // Text color
                )) {
                Text(if (isRecording) "Stop Recording" else "Start Recording")
            }

            // Loading Indicator
            if (isLoading) {
                Text("Processing...",
                    style = MaterialTheme.typography.body1,
                    color = Color.White)
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
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp)) // Apply rounded corners
                    .background(Color.Transparent),
                verticalArrangement = Arrangement.Top
            ) {
                responses.forEach { response ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth() // Make each background span the full width
                            .padding(4.dp) // Add padding between items
                            .clip(RoundedCornerShape(8.dp)) // Rounded corners for each background
                            .background(Color(0xFF2B2D30)) // Background color for each response
                    ) {
                        Text(
                            text = response,
                            style = MaterialTheme.typography.body1.copy(
                                color = Color.White // Text color for contrast
                            ),
                            modifier = Modifier.padding(8.dp) // Padding inside each background
                        )
                    }

                }
            }
        }
    }
}