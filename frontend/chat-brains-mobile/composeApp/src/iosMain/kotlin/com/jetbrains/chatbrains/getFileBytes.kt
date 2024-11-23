package com.jetbrains.chatbrains

// In iosMain
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSData
import platform.Foundation.NSError
import platform.Foundation.NSFileManager
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.dataWithContentsOfFile
import platform.Foundation.getBytes
import kotlin.native.concurrent.freeze
import platform.Foundation.*
import platform.posix.memcpy

actual fun getFileBytes(filePath: String): ByteArray {
    val nsData = NSData.dataWithContentsOfFile(filePath)
        ?: throw IllegalArgumentException("File not found: $filePath")
    return nsData.toByteArray()
}


@OptIn(ExperimentalForeignApi::class)
fun NSData.toByteArray(): ByteArray {
    return bytes?.let { pointer ->
        ByteArray(length.toInt()).apply {
            usePinned { pinned ->
                memcpy(pinned.addressOf(0), pointer, length)
            }
        }
    } ?: ByteArray(0) // Return empty ByteArray if bytes is null
}