package com.jetbrains.chatbrains

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import platform.AVFAudio.AVAudioPlayer
import platform.AVFoundation.*
import platform.Foundation.*

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual suspend fun playAudio(base64String: String) {
    try {
        // Decode Base64 string to NSData
        val audioData = NSData.create(base64String, NSDataBase64DecodingIgnoreUnknownCharacters)
            ?: throw IllegalArgumentException("Invalid Base64 string")

        // Initialize AVAudioPlayer with the decoded data
        val audioPlayer = AVAudioPlayer(data = audioData, error = null)

        // Prepare and play the audio
        audioPlayer.prepareToPlay()
        audioPlayer.play()

        println("Audio playback started successfully.")
    } catch (e: Exception) {
        println("Error playing audio: ${e.message}")
    }
}