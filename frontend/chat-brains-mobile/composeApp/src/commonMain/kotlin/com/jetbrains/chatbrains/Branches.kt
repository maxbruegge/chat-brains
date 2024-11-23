package com.jetbrains.chatbrains

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.jetbrains.chatbrains.networking.NetworkClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.*
import util.NetworkError
import util.onError
import util.onSuccess

@Composable
fun Branches(client: NetworkClient, onNavigate: () -> Unit) {
    // State to hold the list of branches and loading state
    var branches by remember { mutableStateOf<List<String>?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<NetworkError?>(null) }

    // State to track the selected branch
    var selectedBranch by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    // Fetch branches when the screen is displayed
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                client.branches()
                    .onSuccess { branches = it.toList() }
                    .onError { errorMessage = it }
            } catch (e: Exception) {
                errorMessage = NetworkError.SERVER_ERROR
            } finally {
                isLoading = false
            }
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Branches",
            style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (isLoading) {
            // Show a loading indicator
            CircularProgressIndicator(
                modifier = Modifier.align(alignment = androidx.compose.ui.Alignment.CenterHorizontally)
            )
        } else {
            // Show the list of branches
            LazyColumn(modifier = Modifier.fillMaxSize().weight(1f)) {
                items(branches ?: listOf()) { branch ->
                    BranchItem(
                        branch = branch,
                        isSelected = branch == selectedBranch,
                        onSelect = { selectedBranch = branch }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        errorMessage?.let {
            Text(
                text = it.name,
                color = Color.Red,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        Button(
            onClick = onNavigate,
            modifier = Modifier.fillMaxWidth(),
            enabled = selectedBranch != null // Enable only if a branch is selected
        ) {
            Text("Select Branch")
        }
    }
}

@Composable
fun BranchItem(branch: String, isSelected: Boolean, onSelect: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelect() }
            .background(if (isSelected) Color.LightGray else Color.Transparent)
            .padding(16.dp),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
    ) {
        Text(
            text = branch,
            style = MaterialTheme.typography.body1,
            color = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.onSurface
        )
    }
}
