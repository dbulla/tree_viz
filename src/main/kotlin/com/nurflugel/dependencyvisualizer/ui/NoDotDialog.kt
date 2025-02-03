package com.nurflugel.dependencyvisualizer.ui

import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.FlowLayout
import java.awt.FlowLayout.RIGHT
import java.awt.event.ActionEvent
import java.awt.event.KeyEvent.VK_ESCAPE
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.File
import javax.swing.*
import javax.swing.JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT
import javax.swing.JFileChooser.APPROVE_OPTION

open class NoDotDialog(dotExecutablePath: String) : NurflugelDialog() {
    lateinit var file: File
    private lateinit var buttonCancel: JButton
    private lateinit var buttonOK: JButton
    private lateinit var useTextBoxButton: JButton
    private lateinit var contentPane: JPanel
    private lateinit var pathTextField: JTextField
    var wasFileChosen = false

    init {
        buildDialog(dotExecutablePath)
    }

    private fun buildDialog(dotExecutablePath: String?) {
        setupUI()
//        setContentPane(contentPane)
        isModal = true
        getRootPane().defaultButton = buttonOK
        pathTextField.text = dotExecutablePath
        addListeners()

        // call onCancel() when cross is clicked
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        pack()
        center()
        isVisible = true
    }

    override fun addListeners() {
        buttonOK.addActionListener { e: ActionEvent? -> onOK() }
        useTextBoxButton.addActionListener { e: ActionEvent? ->
            file = File(pathTextField.text)
            dispose()
        }
        buttonCancel.addActionListener { e: ActionEvent? -> onCancel() }

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                onCancel()
            }
        })

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction({ e: ActionEvent? -> onCancel() }, KeyStroke.getKeyStroke(VK_ESCAPE, 0), WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    }

    private fun onOK() {
        // valueOf your code here
        val fileChooser = JFileChooser()
        val result = fileChooser.showOpenDialog(this)

        if (result == APPROVE_OPTION) {
            file = fileChooser.selectedFile
        }
        wasFileChosen = true
        dispose()
    }

    private fun onCancel() {
        wasFileChosen = false
        dispose()
    }

    // non-IDE UI stuff
    private fun setupUI() {
        contentPane = JPanel()
        contentPane.layout = BorderLayout(5, 5)

        val labelPanel = JPanel()
        val textPanel = JPanel()
        val buttonPanel = JPanel()

        labelPanel.layout = BorderLayout(5, 5)

        val label1 = JLabel()

        label1.horizontalAlignment = 0
        label1.horizontalTextPosition = 2
        label1.text = "<html>Please use the  \"Open Dialog\" button or the text box to select the executable file in the<br>" +
                      "distribution, or click  \"Cancel\" to exit the application.</html>"
        labelPanel.add(label1, CENTER)

        textPanel.layout = BorderLayout(5, 5)

        pathTextField = JTextField()
        textPanel.add(pathTextField, CENTER)

        buttonPanel.layout = FlowLayout(RIGHT)

        buttonOK = JButton()
        buttonOK.text = "Open Dialog"
        buttonPanel.add(buttonOK)

        useTextBoxButton = JButton()
        useTextBoxButton.text = "Use above path"
        buttonPanel.add(useTextBoxButton)

        buttonCancel = JButton()
        buttonCancel.text = "Cancel"
        buttonPanel.add(buttonCancel)

        contentPane.add(labelPanel, NORTH)
        contentPane.add(textPanel, CENTER)
        contentPane.add(buttonPanel, SOUTH)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            val dialog = NoDotDialog("/opt/homebrew/bin/dot")
            SwingUtilities.invokeLater { NoDotDialog("/opt/homebrew/bin/dot")}

            System.exit(0)
        }
    }
}
