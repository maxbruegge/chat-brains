package com.jetbrains.chatbrains

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.jetbrains.chatbrains.networking.NetworkClient
import kotlinx.coroutines.launch
import util.NetworkError
import util.onError
import util.onSuccess

@Composable
fun Login(client: NetworkClient, onNavigate: () -> Unit) {
    // State to store the username and password input
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember {
        mutableStateOf(false)
    }
    var errorMessage by remember {
        mutableStateOf<NetworkError?>(null)
    }
    val scope = rememberCoroutineScope()

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Login", style = MaterialTheme.typography.h4, modifier = Modifier.padding(bottom = 16.dp))

        // Username TextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        errorMessage?.let {
            Text(
                text = it.name,
                color = Color.Red
            )
        }

        // Login Button
        Button(
            onClick = {
                scope.launch {
                    isLoading = true
                    errorMessage = null

                    client.login(email = email, password = password)
                        .onSuccess {
                            onNavigate()
                        }
                        .onError {
                            errorMessage = it
                        }
                    isLoading = false
                }
            }
        ) {
            Text("Login")
        }
    }
}
