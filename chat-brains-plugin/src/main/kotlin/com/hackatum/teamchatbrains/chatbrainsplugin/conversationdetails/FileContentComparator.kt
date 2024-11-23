package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails

import com.hackatum.teamchatbrains.chatbrainsplugin.models.ChangeType
import com.hackatum.teamchatbrains.chatbrainsplugin.models.FileChange

object FileContentComparator {
    fun computeFileChanges(localContent: String, newContent: String): List<FileChange> {
        val localLines = localContent.lines()
        val newLines = newContent.lines()
        val changes = mutableListOf<FileChange>()

        val maxLines = maxOf(localLines.size, newLines.size)
        for (i in 0 until maxLines) {
            val localLine = localLines.getOrNull(i)
            val newLine = newLines.getOrNull(i)

            when {
                localLine == null -> changes.add(FileChange(newLine ?: "", ChangeType.ADDITION))
                newLine == null -> changes.add(FileChange(localLine, ChangeType.DELETION))
                localLine == newLine -> changes.add(FileChange(localLine, ChangeType.UNCHANGED))
                else -> {
                    changes.add(FileChange(localLine, ChangeType.DELETION))
                    changes.add(FileChange(newLine, ChangeType.ADDITION))
                }
            }
        }
        return changes
    }
}