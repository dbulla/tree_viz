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

open class FindDotDialog(dotExecutablePath: String) : NurflugelDialog() {
    lateinit var file: File
    private lateinit var buttonCancel: JButton
    private lateinit var buttonOK: JButton
    private lateinit var useTextBoxButton: JButton
    private lateinit var mainPanel: JPanel
    private lateinit var pathTextField: JTextField
    var wasFileChosen = false

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            SwingUtilities.invokeLater {
                val findDotDialog = FindDotDialog("/opt/homebrew/bin/dot")
                findDotDialog.isVisible = true
                println(findDotDialog.file.absolutePath)
                findDotDialog.dispose()
            }
        }
    }

    init {
        setupUI()
        buildDialog()
        addListeners()
        pack()

        getRootPane().defaultButton = buttonOK
        pathTextField.text = dotExecutablePath
        center()
        this.isVisible = true
    }

    private fun buildDialog() {
        contentPane = mainPanel
        isModal = true
        getRootPane().defaultButton = buttonOK
        this.defaultCloseOperation = DO_NOTHING_ON_CLOSE
    }

    override fun addListeners() {
        buttonOK.addActionListener { e: ActionEvent? -> onOK() }
        useTextBoxButton.addActionListener { e: ActionEvent? ->
            file = File(pathTextField.text)
            wasFileChosen = true
            dispose()
        }
        buttonCancel.addActionListener { e: ActionEvent? -> onCancel() }

        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                onCancel()
            }
        })

        // call onCancel() on ESCAPE
        mainPanel.registerKeyboardAction({ e: ActionEvent? -> onCancel() }, KeyStroke.getKeyStroke(VK_ESCAPE, 0), WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
    }

    private fun onOK() {
        // valueOf your code here
        val fileChooser = JFileChooser()
        val result = fileChooser.showOpenDialog(this)

        if (result == APPROVE_OPTION) {
            file = fileChooser.selectedFile
        }
        wasFileChosen = true
        pathTextField.text = file.absolutePath
        dispose()
    }

    private fun onCancel() {
        wasFileChosen = false
        dispose()
    }

    // non-IDE UI stuff
    private fun setupUI() {
        mainPanel = JPanel()
        mainPanel.layout = BorderLayout(5, 5)

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

        mainPanel.add(labelPanel, NORTH)
        mainPanel.add(textPanel, CENTER)
        mainPanel.add(buttonPanel, SOUTH)
    }
}
