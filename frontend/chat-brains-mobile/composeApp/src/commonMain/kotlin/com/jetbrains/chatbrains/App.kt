package com.jetbrains.chatbrains

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import chat_brains_mobile.composeapp.generated.resources.Res
import chat_brains_mobile.composeapp.generated.resources.compose_multiplatform
import dev.theolm.record.Record
import dev.theolm.record.config.OutputFormat
import dev.theolm.record.config.OutputLocation
import dev.theolm.record.config.RecordConfig

@Composable
@Preview
fun App() {
    MaterialTheme {
        var isRecording by remember { mutableStateOf(false) }

        Column(Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
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
        }
    }
}