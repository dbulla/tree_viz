/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */
package com.nurflugel.dependencyvisualizer.ui

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.Person
import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.valueOf
import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.values
import java.awt.BorderLayout
import java.awt.BorderLayout.*
import java.awt.Component
import java.awt.GridBagConstraints
import java.awt.GridBagConstraints.BOTH
import java.awt.GridBagConstraints.HORIZONTAL
import java.awt.GridBagLayout
import java.awt.event.ItemEvent
import javax.swing.*
import javax.swing.BorderFactory.createEtchedBorder
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.SwingConstants.RIGHT

class DataEditorUI internal constructor(private val dataSet: BaseDependencyDataSet) : NurflugelDialog() {
    private lateinit var exitButton: JButton
    private lateinit var editExistingButton: JButton
    private lateinit var newDatapointButton: JButton
    private lateinit var itemToEditCombobox: JComboBox<BaseDependencyObject>
    private lateinit var notesText: JTextArea
    private lateinit var potentialParentsList: JList<Person>
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
    private lateinit var potentialSpousesList: JList<Person>
    private lateinit var spousesPanel: JPanel
    private lateinit var birthDateField: JTextField
    private lateinit var deathDateField: JTextField
    private lateinit var displayNameLabel: JLabel
    private lateinit var birthDateLabel: JLabel
    private lateinit var deathDateLabel: JLabel
    private lateinit var notesLabel: JLabel
    private lateinit var spousesFieldLabel: JLabel

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val fakeData = DependencyDataSet()
            SwingUtilities.invokeLater { DataEditorUI(fakeData) }
        }
    }

    init {
        setupUI()
        buildDialog()
        addListeners()
        pack()

        //        setSize(600, 800);
        setHeightToHalfScreen()
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
        val dropdownObjects = getDropdownListWithEmptyTopItem(dataSet.getObjects())
        itemToEditCombobox.setModel(DefaultComboBoxModel(dropdownObjects.toTypedArray<BaseDependencyObject>()))
    }

    // I want the dropdown to have a "blank" top item.
    private fun getDropdownListWithEmptyTopItem(objects: List<BaseDependencyObject>): List<BaseDependencyObject> {
        val dependencyObject = DependencyObject("", "")
        return listOf(dependencyObject) + dataSet.getObjects().sortedBy { it.displayName }
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
        itemToEditCombobox.addItemListener { editExistingDataSelected(it) }
        potentialSpousesList.addListSelectionListener { handleSpouseSelected() }
    }

    /** An item in the spouses listbox has been selected - process that by
     * adding it to the list of spouses and the spouses dropdown.
     */
    private fun handleSpouseSelected() {
        val person = itemToEditCombobox.selectedItem as Person
        val selectedValuesList = potentialSpousesList.selectedValuesList
        // clear out the list of spouses - we'll rebuild it based on the GUI list
        //            person.spouses.clear() // don't - forces selection changed event
        selectedValuesList
            .map { it.name }
            .forEach { (dataSet as FamilyTreeDataSet).addMarriage(person,it) }
        buildSpousesPanel(person)
    }

    private fun editRankings() {
        // todo another dialog to let you add/edit types
    }

    private fun editExistingDataPoint() {
        itemToEditCombobox.isVisible = true
        displayNameField.isEditable = true
        itemToEditDropdownLabel.isVisible = true
        saveEditedButton.isVisible = true
        if (dataSet.isFamilyTree) {
            parentsScrollPane.isVisible = true
            parentsLabel.isVisible = true
        }
    }

    private fun saveEditedData() {
        // todo set dependencies to parents selected for existing data
        //        val currentDatapoint = currentDatapoint

        if (dataSet.isFamilyTree) {
            //            setParents(currentDatapoint) // todo what if null??
            //            setSpouses(currentDatapoint)
            parentsScrollPane.isVisible = false
            parentsLabel.isVisible = false
        }

        setRanking(currentDatapoint)

        // if new data, add to collection? - no, do that with new data button
        displayNameField.isEditable = false
        itemToEditCombobox.isVisible = false
        itemToEditDropdownLabel.isVisible = false
        saveEditedButton.isVisible = false
    }

    private val currentDatapoint: BaseDependencyObject
        get() = itemToEditCombobox.selectedItem as BaseDependencyObject

//    private fun setParents(currentDatapoint: BaseDependencyObject) {
//        currentDatapoint.removeAllDependencies()
//
//        potentialParentsList.selectedValuesList
//            .map { value: Person -> value.name }
//            .forEach { dependency: String -> currentDatapoint.addDependency(dependency) }
//    }

    private fun setRanking(currentDatapoint: BaseDependencyObject) {
        try {
//            val components = rankingsPanel.components
//
//            for (component in components) {
//                val radioButton = component as JRadioButton
//
//                if (radioButton.isSelected) {
//                    val text = radioButton.text
//                    val ranking = valueOf(text)
//
//                    currentDatapoint.ranking = ranking.name
//
//                    break
//                }
//            }

            val jRadioButton = rankingsPanel.components
                .map { it as JRadioButton }
                .firstOrNull { it.isSelected }

            if(jRadioButton != null) {
                val text= jRadioButton.text
                val ranking=valueOf(text)
                currentDatapoint.ranking=ranking.name
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addNewDataPoint() {
        itemToEditCombobox.isVisible = false
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
    private fun editExistingDataSelected(itemEvent: ItemEvent) {
        potentialParentsList.clearSelection()
        potentialSpousesList.clearSelection()

        if (itemEvent.item is BaseDependencyObject) {

            val item = itemEvent.item as BaseDependencyObject
            // filter out item from list

            if (dataSet.isFamilyTree) {
                val personList: Array<Person> = dataSet.getObjects()
                    .filter { o: BaseDependencyObject -> item != o }
                    .map { o: BaseDependencyObject -> o as Person }
                    .sortedBy { it.displayName }
                    .toTypedArray()

                potentialParentsList.setListData(personList)
                potentialSpousesList.setListData(personList)
            }

            val dependenciesNames: Collection<String> = item.dependencies

            displayNameField.text = item.displayName

            val dependencies = getObjectsFromNames(dependenciesNames)

            if (item is Person) {
                setDependenciesSelected(dependencies)
                setSpousesSelected(item as Person)
                birthDateField.text = item.birthDate
                deathDateField.text = item.deathDate
                buildSpousesPanel(item)
                buildParentsPanel(item)
            }
            setRankingButtons(item)

            when {
                item.notes.isEmpty() -> notesText.text = ""
                else                 -> notesText.text = item.notes[0] /* todo fix this! */
            }

            activateScreens(true)
            pack()
            setHeightToHalfScreen()
            validate()
        } // end if
    }

    private fun activateScreens(value: Boolean) {
        listOf(
            potentialParentsList,
            birthDateField,
            deathDateField,
            notesText,
            potentialSpousesList,
            saveEditedButton,
            deleteButton,
            rankingsPanel,
            displayNameLabel,
            parentsLabel,
            birthDateLabel,
            deathDateLabel,
            notesLabel,
            spousesFieldLabel
        ).forEach { it.isEnabled = value }
    }

    private fun getObjectsFromNames(names: Collection<String>): Collection<BaseDependencyObject> {
        val objects = names
            .map(dataSet::objectByName)
            .sorted()

        return objects
    }

    private fun buildSpousesPanel(person: Person) {
        spousesPanel.removeAll()
        (dataSet as FamilyTreeDataSet).getMarriagesForPerson(person)
            .map { it.getSpouse(person.name) }
            .sorted()
            .distinct()
            .map { JLabel(it) }
            .forEach(spousesPanel::add)

        validate()
    }

    // set the parents (dependencies) of the item as selected in the list
    private fun setDependenciesSelected(dependencies: Collection<BaseDependencyObject>) {

        val model = potentialParentsList.model
        val size = model.size
        val matchingIndices: MutableList<Int> = mutableListOf()
        for (index in 0 until size) {
            val elementAt = model.getElementAt(index)
            if (dependencies.contains(elementAt)) {
                matchingIndices.add(index)
            }
        }

        potentialParentsList.setSelectedIndices(matchingIndices.toIntArray())
        matchingIndices.forEach { potentialParentsList.ensureIndexIsVisible(it) }
    }

    private fun setSpousesSelected(person: Person) {
        val spouses = (dataSet as FamilyTreeDataSet).getMarriagesForPerson(person)
            .map { it.getSpouse(person.name) }
        val model = potentialSpousesList.model
        val size = model.size
        val matchingIndicies: MutableList<Int> = mutableListOf()
        for (index in 0 until size) {
            val elementAt = model.getElementAt(index)
            if (spouses.contains(elementAt.name)) {
                matchingIndicies.add(index)
            }
        }

        potentialSpousesList.setSelectedIndices(matchingIndicies.toIntArray())
        matchingIndicies.forEach { potentialSpousesList.ensureIndexIsVisible(it) }
    }

    private fun setRankingButtons(item: BaseDependencyObject) {
        val rankTitle = item.ranking
        val buttons = rankingsPanel.components

        for (component in buttons) {
            val radioButton = component as JRadioButton
            val text = radioButton.text

            try {
                val buttonRanking = valueOf(text)
                val title = valueOf(rankTitle)
                radioButton.isSelected = buttonRanking == title
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
        rankingsPanel.name = "rankingsPanel"
        rankingsPanel.border = createTitledBorder(createEtchedBorder(), "Ranking")

        parentsPanel = JPanel()
        parentsPanel.name = "parentsPanel"
        parentsPanel.border = createTitledBorder(createEtchedBorder(), "Parents")

        spousesPanel = JPanel()
        spousesPanel.name = "spousesPanel"
        spousesPanel.border = createTitledBorder(createEtchedBorder(), "Spouses")

        parentsLabel.horizontalAlignment = 4
        parentsLabel.horizontalTextPosition = 4
        parentsLabel.verticalAlignment = 1

        notesText = JTextArea()
        notesText.name = "Notes"
        displayNameField = JTextField()
        displayNameField.name = "Display Name Field"
        displayNameField.isEditable = false
        itemToEditDropdownLabel.horizontalAlignment = 4
        itemToEditDropdownLabel.isVisible = true
        itemToEditCombobox = JComboBox<BaseDependencyObject>()
        itemToEditCombobox.setInheritsPopupMenu(false)
        itemToEditCombobox.setLightWeightPopupEnabled(true)
        itemToEditCombobox.setMaximumRowCount(30)
        itemToEditCombobox.isVisible = true
        parentsScrollPane = JScrollPane()
        parentsScrollPane.name = "parentsScrollPane"
        potentialParentsList = JList<Person>()
        potentialParentsList.name = "parentsList"
        parentsScrollPane.setViewportView(potentialParentsList)
        potentialSpousesList = JList<Person>()
        potentialSpousesList.name = "spousesList"
        spousesScrollPane = JScrollPane()
        spousesScrollPane.name = "spousesScrollPane"
        spousesScrollPane.setViewportView(potentialSpousesList)

        parentsPanel.add(parentsScrollPane, CENTER)
        spousesPanel.add(spousesScrollPane, CENTER)

        birthDateField = JTextField()
        birthDateField.name = "Birth Date"
        deathDateField = JTextField()
        deathDateField.name = "Death Date"

        topPanel.add(itemToEditDropdownLabel, WEST)
        topPanel.add(itemToEditCombobox, CENTER)
        mainPanel.add(topPanel, NORTH)
        mainPanel.add(attributesPanel, CENTER)

        val constraints = GridBagConstraints()
        constraints.fill = HORIZONTAL
        constraints.fill = BOTH
        constraints.weightx = 1.0
        constraints.gridx = 0

        buttonPanel.add(newDatapointButton, constraints)
        buttonPanel.add(addEditRankingsButton, constraints)
        buttonPanel.add(editExistingButton, constraints)
        buttonPanel.add(saveEditedButton, constraints)
        buttonPanel.add(deleteButton, constraints)
        buttonPanel.add(exitButton, constraints)

        attributesPanel.layout = GridBagLayout()

        var gridY = 0
        var componentHeight = 1
        addGridBagComponent(attributesPanel, displayNameLabel, constraints, 0, gridY, 1, 1, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, displayNameField, constraints, 1, gridY, 1, 1, HORIZONTAL, 0, 0.5)
        gridY += componentHeight
        componentHeight = 10
        addGridBagComponent(attributesPanel, parentsLabel, constraints, 0, gridY, 1, 1, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, parentsScrollPane, constraints, 1, gridY, 1, componentHeight, HORIZONTAL, 0, 0.5)
        gridY += componentHeight
        componentHeight = 1
        addGridBagComponent(attributesPanel, birthDateLabel, constraints, 0, gridY, 1, 1, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, birthDateField, constraints, 1, gridY, 1, 1, HORIZONTAL, 0, 0.5)
        gridY += componentHeight
        addGridBagComponent(attributesPanel, deathDateLabel, constraints, 0, gridY, 1, 1, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, deathDateField, constraints, 1, gridY, 1, 1, HORIZONTAL, 0, 0.5)
        gridY += componentHeight
        componentHeight = 3
        addGridBagComponent(attributesPanel, notesLabel, constraints, 0, gridY, 1, componentHeight, HORIZONTAL, 10, 0.5)
        addGridBagComponent(attributesPanel, notesText, constraints, 1, gridY, 1, componentHeight, HORIZONTAL, 80, 0.5)
        gridY += componentHeight
        componentHeight = 4
        addGridBagComponent(attributesPanel, spousesFieldLabel, constraints, 0, gridY, 1, 1, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, spousesScrollPane, constraints, 1, gridY, 1, componentHeight, HORIZONTAL, 0, 0.5)


        ///////////////////////////////////////
        addGridBagComponent(attributesPanel, rankingsPanel, constraints, 2, 0, 1, 3, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, parentsPanel, constraints, 2, 3, 1, 3, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, spousesPanel, constraints, 2, 6, 1, 3, HORIZONTAL, 0, 0.5)
        addGridBagComponent(attributesPanel, buttonPanel, constraints, 2, 10, 1, 6, HORIZONTAL, 0, 0.5)

    }

    @Suppress("SameParameterValue")
    private fun createLabel(text: String, horizontalAlignment: Int, horizontalTextPosition: Int): JLabel {
        val label = JLabel(text)
        label.name = text
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
        //        println("Adding component ${component.name} with height $gridHeight to row $gridY")
        panel.add(component, constraints)
    }

}
