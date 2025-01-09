/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */
package com.nurflugel.dependencyvisualizer.ui;

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject;
import com.nurflugel.dependencyvisualizer.data.pojos.Person;
import com.nurflugel.dependencyvisualizer.enums.Ranking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static javax.swing.BoxLayout.Y_AXIS;

/**
 *
 */
public class DataEditorUI extends NurflugelDialog {
    private JButton exitButton;
    private JButton editExistingButton;
    private JButton newButton;
    private JComboBox<Person> existingDataCombobox;
    private JTextArea notesText;
    private JList<Person> parentsList;
    private JTextField displayNameField;

    private BaseDependencyDataSet dataSet;
    private JPanel mainPanel;
    private JLabel exitingDataDropdownLabel;
    private JPanel rankingsPanel;
    private JButton saveEditedButton;
    private JButton deleteButton;
    private JPanel parentsPanel;
    private JScrollPane parentsScrollPane;
    private JLabel parentsLabel;
    private JButton addEditRankingsButton;
    private JList<Person> spouseList;
    private JPanel spousesPanel;
    private JTextField birthDateField;
    private JTextField deathDateField;
    private JPanel slaveWrapper;
    private JPanel masterPanel;
    private JLabel displayNameLabel;
    private JLabel birthDateLabel;
    private JLabel deathDateLabel;
    private JLabel notesLabel;
    private JLabel spousesLabel;

    DataEditorUI(BaseDependencyDataSet dataSet) {
        this.dataSet = dataSet;
        buildDialog();
        addListeners();
        pack();

        // setSize(600, 20);
        setHeightToHalfScreen();
        center();
        setVisible(true);
    }

    private void buildDialog() {
        setContentPane(mainPanel);
        setModal(true);
        getRootPane().setDefaultButton(newButton);
        populateDropdowns();
        activateScreens(false);

        // existingDataCombobox.setVisible(false);
        // exitingDataDropdownLabel.setVisible(false);
        // parentsLabel.setVisible(false);
        // parentsScrollPane.setVisible(false);
        BoxLayout rankingsLayout = new BoxLayout(rankingsPanel, Y_AXIS);
        BoxLayout parentsLayout = new BoxLayout(parentsPanel, Y_AXIS);
        BoxLayout spousesLayout = new BoxLayout(spousesPanel, Y_AXIS);

        rankingsPanel.setLayout(rankingsLayout);
        parentsPanel.setLayout(parentsLayout);
        spousesPanel.setLayout(spousesLayout);
        buildRanksPanel();
    }

    @SuppressWarnings("unchecked")
    private void populateDropdowns() {
        List<? extends BaseDependencyObject> objects = dataSet.getObjects();
        List<BaseDependencyObject> dropdownObjects = getDropdownListWithEmptyTopItem(objects);

        existingDataCombobox.setModel(new DefaultComboBoxModel(dropdownObjects.toArray()));
    }

    private List<BaseDependencyObject> getDropdownListWithEmptyTopItem(List<? extends BaseDependencyObject> objects) {
        DependencyObject dependencyObject = new DependencyObject("", "");
        return Stream.concat(Stream.of(dependencyObject), objects.stream().map(o -> (BaseDependencyObject) o)).toList();

    }

    /**
     * Build up the radio button list from the types.
     */
    private void buildRanksPanel() {
        List<Ranking> shapeAttributes = Ranking.Companion.values();
        ButtonGroup buttonGroup = new ButtonGroup();

        Collections.sort(shapeAttributes);
        rankingsPanel.removeAll();

        for (Ranking type : shapeAttributes) {
            JRadioButton button = new JRadioButton(type.getName());

            buttonGroup.add(button);
            rankingsPanel.add(button);
        }
    }

    @Override
    protected void addListeners() {
        exitButton.addActionListener(actionEvent -> setVisible(false));
        editExistingButton.addActionListener(actionEvent -> editExistingDataPoint());
        saveEditedButton.addActionListener(actionEvent -> saveEditedData());
        newButton.addActionListener(actionEvent -> addNewDataPoint());
        addEditRankingsButton.addActionListener(actionEvent -> editRankings());
        existingDataCombobox.addItemListener(this::existingDataSelected);
        spouseList.addListSelectionListener(e -> {
            Person person = (Person) existingDataCombobox.getSelectedItem();

            buildSpousesPanel();  // todo person as arg?
            validate();
        });
        // parentsList.addListSelectionListener(e ->
        // {
        // Person person = (Person) existingDataCombobox.getSelectedItem();
        //
        // buildParentsPanel(person);
        // validate();
        // });
    }

    private void editRankings() {
        // todo another dialog to let you add/edit types
    }

    private void editExistingDataPoint() {
        existingDataCombobox.setVisible(true);
        displayNameField.setEditable(true);
        exitingDataDropdownLabel.setVisible(true);
        saveEditedButton.setVisible(true);
        parentsScrollPane.setVisible(true);
        parentsLabel.setVisible(true);
    }

    private void saveEditedData() {
        // todo set dependencies to parents selected for existing data
        BaseDependencyObject currentDatapoint = getCurrentDatapoint();

        setParents(currentDatapoint);
        setRanking(currentDatapoint);
        setSpouses(currentDatapoint);

        // if new data, add to collection? - no, do that with new data button
        displayNameField.setEditable(false);
        existingDataCombobox.setVisible(false);
        exitingDataDropdownLabel.setVisible(false);
        saveEditedButton.setVisible(false);
        parentsScrollPane.setVisible(false);
        parentsLabel.setVisible(false);
    }

    private void setSpouses(BaseDependencyObject currentDatapoint) {
        if (currentDatapoint instanceof Person person) {

            person.removeAllSpouses();

            spouseList.getSelectedValuesList()
                    .stream()
                    .map(value -> (Person) value)
                    .forEach(person::addSpouse);
        }
    }

    private BaseDependencyObject getCurrentDatapoint() {
        return (BaseDependencyObject) existingDataCombobox.getSelectedItem();
    }

    private void setParents(BaseDependencyObject currentDatapoint) {
        currentDatapoint.removeAllDependencies();

        parentsList.getSelectedValuesList().stream()
                .map(value -> value.name)
                .forEach(currentDatapoint::addDependency);
    }

    private void setRanking(BaseDependencyObject currentDatapoint) {
        try {
            Component[] components = rankingsPanel.getComponents();

            for (Component component : components) {
                JRadioButton radioButton = (JRadioButton) component;

                if (radioButton.isSelected()) {
                    String text = radioButton.getText();
                    Ranking ranking = Ranking.Companion.valueOf(text);

                    currentDatapoint.ranking = ranking.getName();

                    break;
                }
            }
        } catch (Exception e)  // noinspection CallToPrintStackTrace
        {
            e.printStackTrace();
        }
    }

    private void addNewDataPoint() {
        existingDataCombobox.setVisible(false);
        exitingDataDropdownLabel.setVisible(false);
        saveEditedButton.setVisible(true);
        displayNameField.setEditable(true);
        parentsScrollPane.setVisible(true);
        parentsLabel.setVisible(true);

        // get rank from radio button list types
        // OR
        // be able to add new types
        BaseDependencyObject newObject = new DependencyObject(null, null); // todo populate values!!!

        dataSet.add(newObject);
    }

    /**
     * Edit the existing datapoint in the dialog.
     */
    private void existingDataSelected(ItemEvent itemEvent) {
        parentsList.clearSelection();

        if (itemEvent.getItem() instanceof BaseDependencyObject item) {

            // filter out item from list
            Person[] personList = dataSet.getObjects().stream()
                    .filter(o -> !item.equals(o))
                    .map(o -> (Person) o)
                    .toList().toArray(Person[]::new);

            parentsList.setListData(personList);
            spouseList.setListData(personList);

            Collection<String> dependencies = item.getDependencies();

            displayNameField.setText(item.displayName);

            Collection<BaseDependencyObject> objectsFromNames = getObjectsFromNames(dependencies);

            setParentsSelected(objectsFromNames);
            setRankingButtons(item);

            if (item.getNotes().isEmpty()) {
                notesText.setText("");
            } else {
                notesText.setText(item.getNotes().get(0));  // todo fix this!
            }

            spousesPanel.removeAll();

            if (item instanceof Person person) {

                birthDateField.setText(person.getBirthDate());
                deathDateField.setText(person.getDeathDate());
                buildSpousesPanel();
            }

            buildParentsPanel(item);
            activateScreens(true);
            pack();
            setHeightToHalfScreen();
            validate();
        }                                           // end if
    }

    private void activateScreens(boolean value) {
        Stream.of(((JComponent) parentsList),  //
                        birthDateField,       //
                        deathDateField,       //
                        notesText,            //
                        ((JComponent) spouseList),  //
                        saveEditedButton,     //
                        deleteButton,         //
                        rankingsPanel,        //
                        displayNameLabel,     //
                        parentsLabel, birthDateLabel, deathDateLabel, notesLabel, spousesLabel)
                .filter(Objects::nonNull)
                .forEach(c -> c.setEnabled(value));
    }

    private Collection<BaseDependencyObject> getObjectsFromNames(Collection<String> names) {
        List<BaseDependencyObject> objects = names.stream()
                .filter(n -> dataSet.containsKey(n))
                .map(n -> dataSet.get(n))
                .sorted()
                .collect(toList());

        return objects;
    }

    private void buildSpousesPanel() {
        spousesPanel.removeAll();

        List<Person> spouses = spouseList.getSelectedValuesList();

        spouses.stream()
                .map(spouse -> new JLabel(spouse.displayName))
                .forEach(label -> spousesPanel.add(label));

        validate();
    }

    private void setParentsSelected(Collection<BaseDependencyObject> dependencies) {
        List<BaseDependencyObject> listedObjects = dataSet.getObjects().stream()
                .toList();

        // Now we have to make an array of the indexes for the dropdown.
        List<Integer> indices = dependencies.stream()
                .map(listedObjects::indexOf)
                .toList();
        int[] indexes = new int[indices.size()];

        for (int i = indexes.length - 1; i >= 0; i--) {
            indexes[i] = indices.get(i);
            parentsList.ensureIndexIsVisible(indexes[i]);
        }

        parentsList.setSelectedIndices(indexes);
    }

    private void setRankingButtons(BaseDependencyObject item) {
        String rankTitle = item.ranking;
        Component[] buttons = rankingsPanel.getComponents();

        for (Component component : buttons) {
            JRadioButton radioButton = (JRadioButton) component;
            String text = radioButton.getText();

            try {
                Ranking buttonRanking = Ranking.Companion.valueOf(text);

                radioButton.setSelected(buttonRanking.equals(Ranking.Companion.valueOf(rankTitle)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Build up the parent list from the dependencies.
     */
    private void buildParentsPanel(BaseDependencyObject currentDatapoint) {
        parentsPanel.removeAll();

        Collection<String> parents = currentDatapoint.getDependencies();
        Collection<BaseDependencyObject> baseDependencyObjects = getObjectsFromNames(parents);

        baseDependencyObjects.stream()
                .map(BaseDependencyObject::getDisplayName)
                .peek(d -> System.out.println("d = " + d))
                .sorted()
                .map(JLabel::new)
                .forEach(parentLabel -> parentsPanel.add(parentLabel));
    }

    {
        // GUI initializer generated by IntelliJ IDEA GUI Designer
        // >>> IMPORTANT!! <<<
        // DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(1, 2, new java.awt.Insets(0, 0, 0, 0), -1, -1));

        final JPanel panel1 = new JPanel();

        panel1.setLayout(new GridLayoutManager(6, 1, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel1,
                new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        final JPanel panel2 = new JPanel();

        panel2.setLayout(new GridLayoutManager(6, 1, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2,
                new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        newButton = new JButton();
        newButton.setLabel("New Datapoint");
        newButton.setText("New Datapoint");
        panel2.add(newButton,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        editExistingButton = new JButton();
        editExistingButton.setLabel("Edit Existing Datapoint");
        editExistingButton.setText("Edit Existing Datapoint");
        panel2.add(editExistingButton,
                new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        exitButton = new JButton();
        exitButton.setLabel("Back to main app");
        exitButton.setText("Back to main app");
        panel2.add(exitButton,
                new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        saveEditedButton = new JButton();
        saveEditedButton.setLabel("Save Datapoint");
        saveEditedButton.setText("Save Datapoint");
        panel2.add(saveEditedButton,
                new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        deleteButton = new JButton();
        deleteButton.setLabel("Delete Datapoint");
        deleteButton.setText("Delete Datapoint");
        panel2.add(deleteButton,
                new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        addEditRankingsButton = new JButton();
        addEditRankingsButton.setLabel("Edit/Add Rankings");
        addEditRankingsButton.setText("Edit/Add Rankings");
        panel2.add(addEditRankingsButton,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

        final Spacer spacer1 = new Spacer();

        panel1.add(spacer1,
                new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));

        final Spacer spacer2 = new Spacer();

        panel1.add(spacer2,
                new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        rankingsPanel = new JPanel();
        panel1.add(rankingsPanel,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        rankingsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Ranking"));
        parentsPanel = new JPanel();
        panel1.add(parentsPanel,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        parentsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parents"));
        spousesPanel = new JPanel();
        spousesPanel.setLayout(new GridLayoutManager(1, 1, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        panel1.add(spousesPanel,
                new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        spousesPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Spouses"));

        final JPanel panel3 = new JPanel();

        panel3.setLayout(new GridLayoutManager(7, 2, new java.awt.Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(panel3,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

        final JLabel label1 = new JLabel();

        label1.setText("Display name:");
        panel3.add(label1,
                new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false));
        parentsLabel = new JLabel();
        parentsLabel.setHorizontalAlignment(4);
        parentsLabel.setHorizontalTextPosition(4);
        parentsLabel.setText("Parents:");
        parentsLabel.setVerticalAlignment(1);
        panel3.add(parentsLabel,
                new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false));

        final JLabel label2 = new JLabel();

        label2.setText("Notes:");
        panel3.add(label2,
                new GridConstraints(5, 0, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false));
        notesText = new JTextArea();
        panel3.add(notesText,
                new GridConstraints(5, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_GROW,
                        null, new java.awt.Dimension(150, 50), null, 0, false));
        displayNameField = new JTextField();
        displayNameField.setEditable(false);
        panel3.add(displayNameField,
                new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                        null, new java.awt.Dimension(150, -1), null, 0, false));
        exitingDataDropdownLabel = new JLabel();
        exitingDataDropdownLabel.setHorizontalAlignment(4);
        exitingDataDropdownLabel.setText("Item to edit:");
        exitingDataDropdownLabel.setVisible(true);
        panel3.add(exitingDataDropdownLabel,
                new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false));
        existingDataCombobox = new JComboBox();
        existingDataCombobox.setInheritsPopupMenu(false);
        existingDataCombobox.setLightWeightPopupEnabled(true);
        existingDataCombobox.setMaximumRowCount(30);
        existingDataCombobox.setVisible(true);
        panel3.add(existingDataCombobox,
                new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false));
        parentsScrollPane = new JScrollPane();
        panel3.add(parentsScrollPane,
                new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        parentsList = new JList();
        parentsScrollPane.setViewportView(parentsList);

        final JScrollPane scrollPane1 = new JScrollPane();

        panel3.add(scrollPane1,
                new GridConstraints(6, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW,
                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        spouseList = new JList();
        scrollPane1.setViewportView(spouseList);

        final JLabel label3 = new JLabel();

        label3.setText("Spouse(s):");
        panel3.add(label3,
                new GridConstraints(6, 0, 1, 1, GridConstraints.ANCHOR_NORTHEAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED,
                        null, null, null, 0, false));

        final JLabel label4 = new JLabel();

        label4.setText("Birth date:");
        panel3.add(label4,
                new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));

        final JLabel label5 = new JLabel();

        label5.setText("Death date:");
        panel3.add(label5,
                new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_EAST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED, GridConstraints.SIZEPOLICY_FIXED, null,
                        null, null, 0, false));
        birthDateField = new JTextField();
        panel3.add(birthDateField,
                new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                        null, new java.awt.Dimension(150, -1), null, 0, false));
        deathDateField = new JTextField();
        panel3.add(deathDateField,
                new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED,
                        null, new java.awt.Dimension(150, -1), null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
