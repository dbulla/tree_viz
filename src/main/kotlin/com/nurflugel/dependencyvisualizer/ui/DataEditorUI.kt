/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */
package com.nurflugel.dependencyvisualizer.ui

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.Person
import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.valueOf
import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.values
import java.awt.BorderLayout
import java.awt.BorderLayout.CENTER
import java.awt.BorderLayout.NORTH
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import java.util.*
import javax.swing.*
import javax.swing.BorderFactory.createEtchedBorder
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.SwingConstants.RIGHT


/**
 *
 */
class DataEditorUI internal constructor(private val dataSet: BaseDependencyDataSet) : NurflugelDialog() {
    private lateinit var exitButton: JButton
    private lateinit var editExistingButton: JButton
    private lateinit var newDatapointButton: JButton
    private lateinit var existingDataCombobox: JComboBox<BaseDependencyObject>
    private lateinit var notesText: JTextArea
    private lateinit var parentsList: JList<Person>
    private lateinit var displayNameField: JTextField
    private lateinit var mainPanel: JPanel
    private lateinit var itemToEditDropdownLabel: JLabel
    private lateinit var rankingsPanel: JPanel
    private lateinit var saveEditedButton: JButton
    private lateinit var deleteButton: JButton
    private lateinit var parentsPanel: JPanel
    private lateinit var parentsScrollPane: JScrollPane
    private lateinit var spousesScrollPane: JScrollPane
    private lateinit var parentsLabel: JLabel
    private lateinit var addEditRankingsButton: JButton
    private lateinit var spousesList: JList<Person>
    private lateinit var spousesPanel: JPanel
    private lateinit var birthDateField: JTextField
    private lateinit var deathDateField: JTextField
    private lateinit var displayNameLabel: JLabel
    private lateinit var birthDateLabel: JLabel
    private lateinit var deathDateLabel: JLabel
    private lateinit var notesLabel: JLabel
    private lateinit var spousesFieldLabel: JLabel

    init {
        setupUI()
        buildDialog()
        addListeners()
        pack()

        setSize(600, 800);
        //        setHeightToHalfScreen()
        center()
        isVisible = true
    }

    private fun buildDialog() {
        contentPane = mainPanel
        isModal = true
        getRootPane().defaultButton = newDatapointButton
        populateDropdowns()
        activateScreens(false)

        // existingDataCombobox.setVisible(false);
        // exitingDataDropdownLabel.setVisible(false);
        // parentsLabel.setVisible(false);
        // parentsScrollPane.setVisible(false);
        rankingsPanel.layout = BoxLayout(rankingsPanel, Y_AXIS)
        parentsPanel.layout = BoxLayout(parentsPanel, Y_AXIS)
        spousesPanel.layout = BoxLayout(spousesPanel, Y_AXIS)
        buildRanksPanel()
    }

    private fun populateDropdowns() {
        val objects: List<BaseDependencyObject> = dataSet.getObjects().sorted()
        val dropdownObjects = getDropdownListWithEmptyTopItem(objects)

        existingDataCombobox.setModel(DefaultComboBoxModel(dropdownObjects.toTypedArray<BaseDependencyObject>()))
    }

    // I want the dropdown to have a "blank" top item.
    private fun getDropdownListWithEmptyTopItem(objects: List<BaseDependencyObject>): List<BaseDependencyObject> {
        val dependencyObject = DependencyObject("", "")
        return listOf(dependencyObject) + objects
    }

    /** Build up the radio button list from the types. */
    private fun buildRanksPanel() {
        val shapeAttributes = values()
        val buttonGroup = ButtonGroup()

        rankingsPanel.removeAll()

        shapeAttributes
            .sorted()
            .forEach { (name) ->
                val button = JRadioButton(name)
                buttonGroup.add(button)
                rankingsPanel.add(button)
            }
    }

    override fun addListeners() {
        exitButton.addActionListener { isVisible = false }
        editExistingButton.addActionListener { editExistingDataPoint() }
        saveEditedButton.addActionListener { saveEditedData() }
        newDatapointButton.addActionListener { addNewDataPoint() }
        addEditRankingsButton.addActionListener { editRankings() }
        existingDataCombobox.addItemListener { this.existingDataSelected(it) }
        spousesList.addListSelectionListener {
            val person = existingDataCombobox.selectedItem as Person
            buildSpousesPanel() // todo person as arg?
            validate()
        }
        // parentsList.addListSelectionListener(e ->
        // {
        // Person person = (Person) existingDataCombobox.getSelectedItem();
        //
        // buildParentsPanel(person);
        // validate();
        // });
    }

    private fun editRankings() {
        // todo another dialog to let you add/edit types
    }

    private fun editExistingDataPoint() {
        existingDataCombobox.isVisible = true
        displayNameField.isEditable = true
        itemToEditDropdownLabel.isVisible = true
        saveEditedButton.isVisible = true
        parentsScrollPane.isVisible = true
        parentsLabel.isVisible = true

    }

    private fun saveEditedData() {
        // todo set dependencies to parents selected for existing data
//        val currentDatapoint = currentDatapoint

        setParents(currentDatapoint) // todo what if null??
        setRanking(currentDatapoint)
        setSpouses(currentDatapoint)

        // if new data, add to collection? - no, do that with new data button
        displayNameField.isEditable = false
        existingDataCombobox.isVisible = false
        itemToEditDropdownLabel.isVisible = false
        saveEditedButton.isVisible = false
        parentsScrollPane.isVisible = false
        parentsLabel.isVisible = false
    }

    private fun setSpouses(currentDatapoint: BaseDependencyObject?) {
        if (currentDatapoint is Person) {
            currentDatapoint.removeAllSpouses()

            spousesList.selectedValuesList
                .map { value: Person -> value }
                .forEach { spouse: Person -> currentDatapoint.addSpouse(spouse) }
        }
    }

    private val currentDatapoint: BaseDependencyObject
        get() = existingDataCombobox.selectedItem as BaseDependencyObject

    private fun setParents(currentDatapoint: BaseDependencyObject) {
        currentDatapoint.removeAllDependencies()

        parentsList.selectedValuesList
            .map { value: Person -> value.name }
            .forEach { dependency: String -> currentDatapoint.addDependency(dependency) }
    }

    private fun setRanking(currentDatapoint: BaseDependencyObject) {
        try {
            val components = rankingsPanel.components

            for (component in components) {
                val radioButton = component as JRadioButton

                if (radioButton.isSelected) {
                    val text = radioButton.text
                    val ranking = valueOf(text)

                    currentDatapoint.ranking = ranking.name

                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNewDataPoint() {
        existingDataCombobox.isVisible = false
        itemToEditDropdownLabel.isVisible = false
        saveEditedButton.isVisible = true
        displayNameField.isEditable = true
        parentsScrollPane.isVisible = true
        parentsLabel.isVisible = true

        // get rank from radio button list types
        // OR
        // be able to add new types
        val newObject: BaseDependencyObject = DependencyObject("null", "null") // todo populate values!!!

        dataSet.add(newObject)
    }

    /**
     * Edit the existing datapoint in the dialog.
     */
    private fun existingDataSelected(itemEvent: ItemEvent) {
        parentsList.clearSelection()

        if (itemEvent.item is BaseDependencyObject) {

            val item = itemEvent.item as BaseDependencyObject
            // filter out item from list

            val personList: Array<Person?> = dataSet.getObjects()
                .filter { o: BaseDependencyObject -> item != o }
                .map { o: BaseDependencyObject? -> o as Person? }
                .toTypedArray()

            parentsList.setListData(personList)
            spousesList.setListData(personList)

            val dependencies: Collection<String> = item.dependencies

            displayNameField.text = item.displayName

            val objectsFromNames = getObjectsFromNames(dependencies)

            setParentsSelected(objectsFromNames)
            setRankingButtons(item)

            when {
                item.notes.isEmpty() -> notesText.text = ""
                else                 -> {
                    notesText.text = item.notes[0] // todo fix this!
                }
            }

            spousesPanel.removeAll()

            if (item is Person) {
                birthDateField.text = item.birthDate
                deathDateField.text = item.deathDate
                buildSpousesPanel()
            }

            buildParentsPanel(item)
            activateScreens(true)
            pack()
            setHeightToHalfScreen()
            validate()
        } // end if
    }

    private fun activateScreens(value: Boolean) {
        listOf(
            parentsList,  //
            birthDateField,  //
            deathDateField,  //
            notesText,  //
            spousesList,  //
            saveEditedButton,  //
            deleteButton,  //
            rankingsPanel,  //
            displayNameLabel,  //
            parentsLabel,  //
            birthDateLabel,  //
            deathDateLabel,  //
            notesLabel,  //
            spousesFieldLabel
        )
            .filter(Objects::nonNull)
            .forEach { it.isEnabled = value }
    }

    private fun getObjectsFromNames(names: Collection<String>): Collection<BaseDependencyObject> {
        val objects = names
            .filter(dataSet::containsKey)
            .map(dataSet::get)
            .sorted()

        return objects
    }

    private fun buildSpousesPanel() {
        spousesPanel.removeAll()

        spousesList.selectedValuesList
            .map { spouse: Person -> JLabel(spouse.displayName) }
            .forEach(spousesPanel::add)
        validate()
    }

    private fun setParentsSelected(dependencies: Collection<BaseDependencyObject>) {
        val listedObjects = dataSet.getObjects()

        // Now we have to make an array of the indexes for the dropdown.
        val indices = dependencies
            .map(listedObjects::indexOf)

        val indexes = IntArray(indices.size)

        indexes.indices.reversed().forEach { i ->
            indexes[i] = indices[i]
            parentsList.ensureIndexIsVisible(indexes[i])
        }

        parentsList.selectedIndices = indexes
    }

    private fun setRankingButtons(item: BaseDependencyObject) {
        val rankTitle = item.ranking
        val buttons = rankingsPanel.components

        for (component in buttons) {
            val radioButton = component as JRadioButton
            val text = radioButton.text

            try {
                val buttonRanking = valueOf(text)

                radioButton.isSelected = buttonRanking == valueOf(rankTitle)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Build up the parent list from the dependencies.
     */
    private fun buildParentsPanel(currentDatapoint: BaseDependencyObject) {
        parentsPanel.removeAll()

        val parents: Collection<String> = currentDatapoint.dependencies
        val baseDependencyObjects = getObjectsFromNames(parents)

        baseDependencyObjects
            .map(BaseDependencyObject::displayName)
            .sorted()
            .map(::JLabel)
            .forEach(parentsPanel::add)
    }

    private fun setupUI() {
        mainPanel = JPanel(BorderLayout())
        val topPanel = JPanel(BorderLayout())
        birthDateLabel = createLabel("Birth date:", RIGHT, RIGHT)
        displayNameLabel = createLabel("Display name:", RIGHT, RIGHT)
        parentsLabel = createLabel("Parents:", RIGHT, RIGHT)
        notesLabel = createLabel("Notes:", RIGHT, RIGHT)
        itemToEditDropdownLabel = createLabel("Item to edit:", RIGHT, RIGHT)
        spousesFieldLabel = createLabel("Spouse(s):", RIGHT, RIGHT)
        deathDateLabel = createLabel("Death date:", RIGHT, RIGHT)


        val attributesPanel = JPanel()
        attributesPanel.border = createTitledBorder(createEtchedBorder(), "Attributes")
        val buttonPanel = JPanel()
        newDatapointButton = JButton("New Datapoint")
        editExistingButton = JButton("Edit Existing Datapoint")
        exitButton = JButton("Back to main app")
        saveEditedButton = JButton("Save Datapoint")
        deleteButton = JButton("Delete Datapoint")
        addEditRankingsButton = JButton("Edit/Add Rankings")

        buttonPanel.layout = GridBagLayout()
        rankingsPanel = JPanel()
        rankingsPanel.border = createTitledBorder(createEtchedBorder(), "Ranking")

        parentsPanel = JPanel()
        parentsPanel.border = createTitledBorder(createEtchedBorder(), "Parents")

        spousesPanel = JPanel()
        spousesPanel.border = createTitledBorder(createEtchedBorder(), "Spouses")

        parentsLabel.horizontalAlignment = 4
        parentsLabel.horizontalTextPosition = 4
        parentsLabel.verticalAlignment = 1

        notesText = JTextArea()
        displayNameField = JTextField()
        displayNameField.isEditable = false
        itemToEditDropdownLabel.horizontalAlignment = 4
        itemToEditDropdownLabel.isVisible = true
        existingDataCombobox = JComboBox<BaseDependencyObject>()
        existingDataCombobox.setInheritsPopupMenu(false)
        existingDataCombobox.setLightWeightPopupEnabled(true)
        existingDataCombobox.setMaximumRowCount(30)
        existingDataCombobox.isVisible = true
        parentsScrollPane = JScrollPane()
        parentsList = JList<Person>()
        parentsScrollPane.setViewportView(parentsList)
        spousesList = JList<Person>()
        spousesScrollPane = JScrollPane()
        spousesScrollPane.setViewportView(spousesList)

        parentsPanel.add(parentsScrollPane, CENTER)
        spousesPanel.add(spousesScrollPane, CENTER)

        birthDateField = JTextField()
        deathDateField = JTextField()

        topPanel.add(itemToEditDropdownLabel, BorderLayout.WEST)
        topPanel.add(existingDataCombobox, CENTER)
        mainPanel.add(topPanel, NORTH)
        mainPanel.add(attributesPanel, CENTER)

        val constraints = GridBagConstraints()
        constraints.fill = HORIZONTAL
        constraints.weightx = 1.0
        constraints.gridx = 0

        buttonPanel.add(newDatapointButton, constraints)
        buttonPanel.add(addEditRankingsButton, constraints)
        buttonPanel.add(editExistingButton, constraints)
        buttonPanel.add(saveEditedButton, constraints)
        buttonPanel.add(deleteButton, constraints)
        buttonPanel.add(exitButton, constraints)

        attributesPanel.layout = GridBagLayout()

        addGridBagComponent(attributesPanel, displayNameLabel, constraints, 0, 0, 1, 1, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, displayNameField, constraints, 1, 0, 1, 1, HORIZONTAL, 0, 0.5)

        addGridBagComponent(attributesPanel, parentsLabel, constraints, 0, 1, 1, 1, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, parentsScrollPane, constraints, 1, 1, 1, 4, HORIZONTAL, 0, 0.5)

        addGridBagComponent(attributesPanel, rankingsPanel, constraints, 2, 0, 1, 3, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, parentsPanel, constraints, 2, 2, 1, 3, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, spousesPanel, constraints, 2, 5, 1, 3, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, buttonPanel, constraints, 2, 9, 1, 6, HORIZONTAL, 0, 0.5)

        addGridBagComponent(attributesPanel, birthDateLabel, constraints, 0, 5, 1, 1, HORIZONTAL, 10, 0.5)
        addGridBagComponent(attributesPanel, birthDateField, constraints, 1, 5, 1, 1, HORIZONTAL, 10, 0.5)

        addGridBagComponent(attributesPanel, deathDateLabel, constraints, 0, 6, 1, 1, HORIZONTAL, 10, 0.5)
        addGridBagComponent(attributesPanel, deathDateField, constraints, 1, 6, 1, 1, HORIZONTAL, 10, 0.5)

        addGridBagComponent(attributesPanel, notesLabel, constraints, 0, 7, 1, 1, HORIZONTAL, 10, 0.5)
        addGridBagComponent(attributesPanel, notesText, constraints, 1, 7, 1, 3, HORIZONTAL, 10, 0.5)

        addGridBagComponent(attributesPanel, spousesFieldLabel, constraints, 0, 11, 1, 1, HORIZONTAL, 10, 0.5)
        addGridBagComponent(attributesPanel, spousesScrollPane, constraints, 1, 11, 1, 4, HORIZONTAL, 10, 0.5)
    }

    @Suppress("SameParameterValue")
    private fun createLabel(text: String, horizontalAlignment: Int, horizontalTextPosition: Int): JLabel {
        val label = JLabel(text)
        label.horizontalAlignment = horizontalAlignment
        label.horizontalTextPosition = horizontalTextPosition
        return label
    }

    @Suppress("SameParameterValue")
    private fun addGridBagComponent(
        panel: JPanel,
        component: Component,
        constraints: GridBagConstraints,
        gridX: Int,
        gridY: Int,
        gridWidth: Int,
        gridHeight: Int,
        fill: Int,
        ipadY: Int,
        weightX: Double,
    ) {
        constraints.fill = fill
        constraints.gridx = gridX
        constraints.gridy = gridY
        constraints.weightx = weightX
        constraints.gridwidth = gridWidth
        constraints.gridheight = gridHeight
        constraints.ipady = ipadY
        panel.add(component, constraints)
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val fakeData = DependencyDataSet()
            val ui = DataEditorUI(fakeData)
        }
    }
}
