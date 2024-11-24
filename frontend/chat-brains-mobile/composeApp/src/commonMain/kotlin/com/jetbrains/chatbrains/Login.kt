package com.jetbrains.chatbrains

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
            .background(Color(0xFF1E1F22))
            .padding(16.dp),
    verticalArrangement = Arrangement.Center
    ) {
        // Username TextField
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Username") },
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                backgroundColor = Color(0xFF2B2D30), // Custom background
                cursorColor = Color(0xFF377287),
                focusedIndicatorColor = Color.Transparent, // Underline color when focused
                unfocusedIndicatorColor = Color.Transparent, // Underline color when unfocused
                disabledIndicatorColor = Color.DarkGray, // Underline when disabled
                errorIndicatorColor = Color.Red, // Underline when there's an error
                leadingIconColor = Color(0xFF377287), // Leading icon color
                trailingIconColor = Color(0xFF377287), // Trailing icon color
                focusedLabelColor = Color(0xFF377287), // Label color when focused
                unfocusedLabelColor = Color.LightGray, // Label color when unfocused
                disabledLabelColor = Color.Gray, // Label color when disabled
                errorLabelColor = Color.Red, // Label color in error state
                placeholderColor = Color.White.copy(alpha = 0.5f), // Placeholder text color
                disabledPlaceholderColor = Color.Gray // Placeholder text when disabled
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password TextField
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            colors = TextFieldDefaults.textFieldColors(
                textColor = Color.White,
                backgroundColor = Color(0xFF2B2D30), // Custom background
                cursorColor = Color(0xFF377287),
                focusedIndicatorColor = Color.Transparent, // Underline color when focused
                unfocusedIndicatorColor = Color.Transparent, // Underline color when unfocused
                disabledIndicatorColor = Color.DarkGray, // Underline when disabled
                errorIndicatorColor = Color.Red, // Underline when there's an error
                leadingIconColor = Color(0xFF377287), // Leading icon color
                trailingIconColor = Color(0xFF377287), // Trailing icon color
                focusedLabelColor = Color(0xFF377287), // Label color when focused
                unfocusedLabelColor = Color.LightGray, // Label color when unfocused
                disabledLabelColor = Color.Gray, // Label color when disabled
                errorLabelColor = Color.Red, // Label color in error state
                placeholderColor = Color.White.copy(alpha = 0.5f), // Placeholder text color
                disabledPlaceholderColor = Color.Gray // Placeholder text when disabled
            ),
            shape = RoundedCornerShape(12.dp),
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
            },
            modifier = Modifier
                .height(64.dp)
                .fillMaxWidth() // Make the button span the full width
                .padding(top = 16.dp), // Add padding above the button
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color(0xFF377287), // Custom background color
                contentColor = Color.White // Text color inside the button
            ),
            shape = RoundedCornerShape(12.dp)

        ) {
            Text("Login")
        }
    }
}
