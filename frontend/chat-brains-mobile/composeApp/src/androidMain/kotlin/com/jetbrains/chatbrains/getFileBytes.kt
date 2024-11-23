package com.jetbrains.chatbrains

import java.io.File
import java.io.FileNotFoundException

actual fun getFileBytes(filePath: String): ByteArray {
    val file = File(filePath)
    if (!file.exists()) {
        throw FileNotFoundException("File not found at path: $filePath")
    }
    return file.readBytes()
}
