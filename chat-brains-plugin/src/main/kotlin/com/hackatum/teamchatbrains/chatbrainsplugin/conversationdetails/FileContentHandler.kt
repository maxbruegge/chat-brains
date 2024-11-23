package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.ui.components.JBPanel
import javax.swing.JScrollPane

object FileContentHandler {
    fun getLocalFileContent(project: Project, filePath: String): String? {
        val virtualFile = LocalFileSystem.getInstance().findFileByPath("${project.basePath}/$filePath")
        return virtualFile?.contentsToByteArray()?.toString(Charsets.UTF_8)
    }

    fun applyFileChanges(project: Project, fileChangesPanel: JBPanel<*>) {
        val successMessages = mutableListOf<String>()
        val errorMessages = mutableListOf<String>()

        for (component in fileChangesPanel.components) {
            if (component is JScrollPane) {
                val filePath = component.getClientProperty("filePath") as? String
                val fileContent = component.getClientProperty("fileContent") as? String

                if (filePath != null && fileContent != null) {
                    val virtualFile = LocalFileSystem.getInstance().findFileByPath("${project.basePath}/$filePath")
                    if (virtualFile != null && virtualFile.isWritable) {
                        com.intellij.openapi.application.ApplicationManager.getApplication().runWriteAction {
                            try {
                                virtualFile.setBinaryContent(fileContent.toByteArray())
                                successMessages.add("Successfully updated $filePath")
                            } catch (e: Exception) {
                                errorMessages.add("Failed to update $filePath: ${e.message}")
                            }
                        }
                    } else {
                        errorMessages.add("File not writable or missing: $filePath")
                    }
                }
            }
        }

        val messageBuilder = StringBuilder()
        if (successMessages.isNotEmpty()) {
            messageBuilder.append("Success:\n")
            messageBuilder.append(successMessages.joinToString("\n"))
            messageBuilder.append("\n\n")
        }
        if (errorMessages.isNotEmpty()) {
            messageBuilder.append("Errors:\n")
            messageBuilder.append(errorMessages.joinToString("\n"))
        }

        Messages.showInfoMessage(messageBuilder.toString(), "File Changes Summary")
    }
}