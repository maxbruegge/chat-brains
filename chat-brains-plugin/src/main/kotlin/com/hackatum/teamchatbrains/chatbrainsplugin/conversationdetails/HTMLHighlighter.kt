package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails

import com.hackatum.teamchatbrains.chatbrainsplugin.models.ChangeType
import com.hackatum.teamchatbrains.chatbrainsplugin.models.FileChange

object HTMLHighlighter {
    fun highlightContent(changes: List<FileChange>): String {
        val builder = StringBuilder("<html><body style='font-family:monospace;'>")
        changes.forEach { change ->
            when (change.type) {
                ChangeType.ADDITION -> builder.append(
                    "<div style='color: #babbba55color; background-color: rgba(255, 0, 0, 0.08); padding: 2px;'>+ ${change.line.escapeHtml()}</div>"
                )
                ChangeType.DELETION -> builder.append(
                    "<div style='color: #babbba55; background-color: rgba(0, 255, 0, 0.07); padding: 2px;'>- ${change.line.escapeHtml()}</div>"
                )
                ChangeType.UNCHANGED -> builder.append(
                    "<div style='color: #babbba55; padding: 2px;'>&nbsp;&nbsp;${change.line.escapeHtml()}</div>"
                )
            }
        }
        builder.append("</body></html>")
        return builder.toString()
    }

    private fun String.escapeHtml(): String {
        return this.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
}