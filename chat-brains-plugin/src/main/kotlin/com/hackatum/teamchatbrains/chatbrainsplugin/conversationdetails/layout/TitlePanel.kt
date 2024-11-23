package com.hackatum.teamchatbrains.chatbrainsplugin.conversationdetails.layout

import com.intellij.ui.components.JBLabel
import javax.swing.BoxLayout
import javax.swing.JPanel

object TitlePanel {

    fun create(): JPanel {
        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)

            add(JBLabel("").apply {
                name = "summaryLabel"
                border = javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0)
            })
            add(JBLabel("").apply {
                name = "summaryText"
                border = javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0)
            })
            add(JBLabel("").apply {
                name = "fileChangesTitle"
                border = javax.swing.BorderFactory.createEmptyBorder(0, 0, 10, 0)
            })
        }
    }

    fun update(detailsPanel: JPanel, title: String, summary: String) {
        val titlePanel = detailsPanel.getComponent(0) as JPanel
        val summaryLabel = titlePanel.getComponent(0) as JBLabel
        val summaryText = titlePanel.getComponent(1) as JBLabel
        val fileChangesTitle = titlePanel.getComponent(2) as JBLabel

        summaryLabel.text = "<html><b>Conversation:</b>&nbsp;&nbsp;$title</html>"
        summaryText.text = "<html><b>Summary:</b><i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;$summary</i></html>"
        fileChangesTitle.text = "<html><b>File changes:</b></html>"
    }
}