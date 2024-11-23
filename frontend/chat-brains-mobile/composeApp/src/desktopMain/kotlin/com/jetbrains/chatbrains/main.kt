package com.jetbrains.chatbrains

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "chat-brains-mobile",
    ) {
        App()
    }
}