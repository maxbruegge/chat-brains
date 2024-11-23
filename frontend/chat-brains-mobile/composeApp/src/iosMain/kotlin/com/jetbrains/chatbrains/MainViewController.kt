package com.jetbrains.chatbrains

import androidx.compose.runtime.remember
import androidx.compose.ui.window.ComposeUIViewController
import com.jetbrains.chatbrains.networking.NetworkClient
import com.jetbrains.chatbrains.networking.createHttpClient
import io.ktor.client.engine.darwin.Darwin

fun MainViewController() = ComposeUIViewController { App(
    client = remember {
        NetworkClient(createHttpClient(Darwin.create()))
    }
) }