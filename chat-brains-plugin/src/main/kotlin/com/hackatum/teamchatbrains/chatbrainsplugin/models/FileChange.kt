package com.hackatum.teamchatbrains.chatbrainsplugin.models

data class FileChange(
    val line: String,
    val type: ChangeType
)