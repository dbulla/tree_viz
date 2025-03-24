package com.nurflugel.dependencyvisualizer.ui

import com.nurflugel.dependencyvisualizer.data.DataHandler
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.DOWN
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.UP
import com.nurflugel.dependencyvisualizer.enums.OutputFormat
import com.nurflugel.dependencyvisualizer.enums.Ranking
import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.clearRankings
import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.values
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.*
import java.awt.BorderLayout.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.prefs.Preferences
import javax.swing.*
import javax.swing.BorderFactory.createEtchedBorder
import javax.swing.BorderFactory.createTitledBorder
import javax.swing.BoxLayout.X_AXIS
import javax.swing.BoxLayout.Y_AXIS
import javax.swing.JFileChooser.APPROVE_OPTION
import javax.swing.border.Border
import javax.swing.border.EtchedBorder
import kotlin.system.exitProcess


/**
 *
 */
class LoadersUi private constructor() : JFrame() {
    var version: String = "1.0.0"
    private lateinit var quitButton: JButton

    private lateinit var makeGraphButton: JButton
    private lateinit var findDotButton: JButton
    private lateinit var loadDatafileButton: JButton
    private lateinit var filterDownCheckBox: JCheckBox
    private lateinit var filterUpCheckBox: JCheckBox
    private lateinit var rankingCheckBox: JCheckBox
    private lateinit var preferences: Preferences
    private val busyCursor: Cursor = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR)
    private val normalCursor: Cursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR)

    /**
     * Get the dot executable path if it already exists in Preferences, or is installed. If not easily findable, as the user where the hell he put it.
     */
    private lateinit var dotExecutablePath: String
    private val os: String = System.getProperty("os.name")
    private lateinit var filtersPanel: JPanel
    private val listCollection: MutableList<JList<*>> = mutableListOf()
    private lateinit var editDataButton: JButton
    private lateinit var dataHandler: DataHandler
    private lateinit var newDataButton: JButton
    private lateinit var familyTreeCheckBox: JCheckBox
    private lateinit var clearFiltersButton: JButton
    private lateinit var saveDataFileButton: JButton
    private lateinit var dataSet: BaseDependencyDataSet

    init {
        initializeComponents()
        addListeners()
    }

    private fun initializeComponents() {
        setupUI()
        makeGraphButton.isEnabled = false
        retrieveSettings()
        title = "Dibble! 1.0!!"
        pack()
        center()
        isVisible = true
    }

    private fun retrieveSettings() {
        preferences = Preferences.userNodeForPackage(LoadersUi::class.java)
        dotExecutablePath = preferences.get(DOT_EXECUTABLE, "").trim()
        rankingCheckBox.isSelected = preferences.get(USE_RANKING, "true").toBoolean()
        filterUpCheckBox.isSelected = preferences.get(FILTER_UP, "false").toBoolean()
        filterDownCheckBox.isSelected = preferences.get(FILTER_DOWN, "false").toBoolean()
        familyTreeCheckBox.isSelected = preferences.get(FAMILY_TREE, "true").toBoolean()
    }

    private fun center() {
        val defaultToolkit = Toolkit.getDefaultToolkit()
        val screenSize = defaultToolkit.screenSize
        val x = ((screenSize.getWidth() - width) / 2).toInt()
        val y = ((screenSize.getHeight() - height) / 2).toInt()
        val loadersUi = this

        loadersUi.setBounds(x, y, width, height)
    }

    private fun addListeners() {
        familyTreeCheckBox.addActionListener { _ -> dataSet.isFamilyTree = familyTreeCheckBox.isSelected }
        clearFiltersButton.addActionListener { _ -> listCollection.forEach { it.clearSelection() } }
        quitButton.addActionListener { _ -> quitAction() }
        makeGraphButton.addActionListener { _ -> makeGraph() }
        findDotButton.addActionListener { _ -> findDotExecutablePath() }
        loadDatafileButton.addActionListener { _ -> loadDatafile() }
        editDataButton.addActionListener { _ -> DataEditorUI(dataSet) }
        newDataButton.addActionListener { _ -> println() } // todo ask if family tree or not, what to call it - then save it
        saveDataFileButton.addActionListener { _ -> dataHandler.saveDataset() }
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent) {
                super.windowClosing(e)
                quitAction()
            }
        })
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun makeGraph() {
        dataHandler.initialize()
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.setRanking(rankingCheckBox.isSelected)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setTypesFilters(ArrayList())

        val dotFile = dataHandler.writeObjects()
        val outputFilePath = convertDataFile(dotFile);

        showImage(outputFilePath);
    }

    private val directionalFilters: List<DirectionalFilter>
        get() {
            val filters: MutableList<DirectionalFilter> = ArrayList()

            if (filterUpCheckBox.isSelected && filterDownCheckBox.isSelected) {
                return filters
            }

            if (filterUpCheckBox.isSelected) {
                filters.add(UP)
            }

            if (filterDownCheckBox.isSelected) {
                filters.add(DOWN)
            }

            return filters
        }

    private val keyObjects: List<BaseDependencyObject>
        get() {
            val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

            listCollection.flatMap { it.selectedValuesList }.forEach { keyObjects.add(it as BaseDependencyObject) }
            return keyObjects
        }

    @Throws(IOException::class, InterruptedException::class)
    private fun convertDataFile(dotFile: File): String {
        val outputFileName = getOutputFileName(dotFile, outputFormat.extension)
        val outputFile = File(dotFile.parent, outputFileName)
        val dotFilePath = dotFile.absolutePath
        val outputFilePath = outputFile.absolutePath
        if (outputFile.exists()) {
            outputFile.delete() // delete the file before generating it if it exists
        }

        if (dotExecutablePath.isEmpty()) {
            findDotExecutablePath()
        }

        val outputFormat = outputFormat.type
        val start = Instant.now()

        if (isWindows) {
            val quote = if (isWindows) "\""
            else ""
            val dot = quote + dotExecutablePath + quote
            val output = " -o $quote$outputFilePath$quote"
            runProcess(mutableListOf(dot, "-T$outputFormat", "$quote$dotFilePath$quote$output"))
        }
        else {
            runProcess(mutableListOf(dotExecutablePath, "-T$outputFormat", "-T$outputFormat", dotFilePath, "-o$outputFilePath"))
        }

        logger.debug("Took {} milliseconds to generate graphic", Duration.between(start, Instant.now()).toMillis())

        return outputFilePath
    }

    /**
     * Takes something like build.dot and returns build.png.
     */
    private fun getOutputFileName(dotFile: File, outputExtension: String): String {
        var results = dotFile.name
        val index = results.indexOf(".dot")

        results = results.substring(0, index) + outputExtension

        return results
    }

    private val isWindows: Boolean
        get() = os.lowercase(Locale.getDefault()).startsWith(WINDOWS)

    private fun showImage(outputFilePath: String) {
        try {
            val commandList: MutableList<String> = ArrayList()

            if (isWindows) {
                commandList.add("cmd.exe")
                commandList.add("/c")
                commandList.add(outputFilePath)
            }
            else if (isOsX) {
                commandList.add("open")
                commandList.add(outputFilePath)
            }
            runProcess(commandList)
        } catch (e: Exception) {
            logger.error("Error", e)
        }
    }

    private fun runProcess(commandList: MutableList<String>) {
        val process = ProcessBuilder(commandList).start() // grab the error stream and print that to stdout so we can see any errors
        val bre = BufferedReader(InputStreamReader(process.errorStream))
        var line: String? = bre.readLine()
        while (line != null) {
            println(line)
            line = bre.readLine()
        }
    }

    private val isOsX: Boolean
        get() {
            return System.getProperty("os.name").lowercase().startsWith("mac os")
        }

    private fun findDotExecutablePath() {
        dotExecutablePath = preferences[DOT_EXECUTABLE, ""]

        if (dotExecutablePath.isEmpty()) {
            dotExecutablePath = when { // try default install
                File(OSX_DOT_LOCATION).exists()          -> OSX_DOT_LOCATION // try homebrew
                File(OSX_HOMEBREW_DOT_LOCATION).exists() -> OSX_HOMEBREW_DOT_LOCATION
                else                                     -> WINDOWS_DOT_LOCATION
            }
        }

        // Create a file chooser
        val dialog = NoDotDialog(dotExecutablePath)

        if (dialog.wasFileChosen) {
            dotExecutablePath = dialog.file.absolutePath
            preferences.put(DOT_EXECUTABLE, dotExecutablePath)
        }
    }

    private fun loadDatafile() {
        val fileChooser = JFileChooser()
        val filter = ExampleFileFilter()

        filter.addExtension("txt")
        filter.addExtension("json")
        filter.setDescription("data files")
        fileChooser.fileFilter = filter

        val lastDir = preferences[LAST_DIR, ""]

        if (lastDir != null) {
            fileChooser.currentDirectory = File(lastDir)
        }

        fileChooser.isMultiSelectionEnabled = false

        val returnVal = fileChooser.showOpenDialog(this)

        if (returnVal == APPROVE_OPTION) {
            loadDatafile(fileChooser.selectedFile)
        }
    }

    private fun loadDatafile(selectedFile: File) { // load data
        //        var selectedFile = file
        cursor = busyCursor

        // file might be null, skip this if so
        preferences.put(LAST_DIR, selectedFile.parent)
        makeGraphButton.isEnabled = true
        clearRankings()
        dataHandler = DataHandler(selectedFile)
        dataHandler.loadDataset()
        dataSet = dataHandler.dataset
        familyTreeCheckBox.isSelected = dataSet.isFamilyTree
        populateDropdowns()
        editDataButton.isEnabled = true
        saveDataFileButton.isEnabled = true
        makeGraphButton.isEnabled = true

        pack()
        center()
        cursor = normalCursor
    }

    private fun populateDropdowns() {
        val shapeAttributes = values()

        filtersPanel.removeAll()
        listCollection.clear()
        filtersPanel.layout = GridLayout((shapeAttributes.size / 2) + 1, 2)

        for (type in shapeAttributes) {
            val filteredObjects = getObjectsForType(type)
            val dependencyObjectJList = JList(filteredObjects)
            listCollection.add(dependencyObjectJList)
            val scrollPane = JScrollPane(dependencyObjectJList)
            scrollPane.preferredSize = Dimension(400, 600)
            val borderPanel = JPanel(BorderLayout())
            val border: Border = createTitledBorder(EtchedBorder(), type.name)

            borderPanel.border = border
            borderPanel.add(scrollPane, CENTER)
            filtersPanel.add(borderPanel)
        }
    }

    @Suppress("SENSELESS_COMPARISON")
    private fun getObjectsForType(type: Ranking): Array<BaseDependencyObject> {
        val filteredObjects: MutableList<BaseDependencyObject> = ArrayList()

        filteredObjects.add(DependencyObject("", type.name))
        filteredObjects.addAll(dataSet.getObjects().filter { it.ranking != null }.sortedBy { it.name.uppercase() }.filter { it.ranking == type.name }.toList())

        return filteredObjects.toTypedArray<BaseDependencyObject>()
    }

    private fun quitAction() {
        saveSettings()
        exitProcess(0)
    }

    private fun saveSettings() {
        preferences.putBoolean(USE_RANKING, rankingCheckBox.isSelected)
        preferences.putBoolean(FILTER_UP, filterUpCheckBox.isSelected)
        preferences.putBoolean(FILTER_DOWN, filterDownCheckBox.isSelected)
        preferences.putBoolean(FAMILY_TREE, familyTreeCheckBox.isSelected)
    }

    private val outputFormat: OutputFormat
        get() {
            if (isOsX) {
                return OutputFormat.getDefaultExtension()
            } // todo fix - get from settings
            return OutputFormat.getDefaultExtension()
        }

    private fun setDefaultDotLocation() {
        dotExecutablePath = preferences[DOT_EXECUTABLE, ""]

        if (dotExecutablePath.isEmpty()) {
            dotExecutablePath = if (os.startsWith(MAC_OS)) {
                OSX_DOT_LOCATION
            }
            else  // if (os.toLowerCase().startsWith("windows"))
            {
                WINDOWS_DOT_LOCATION
            }
        }
    }

    private fun setupUI() {
        contentPane.layout = BorderLayout(5, 5)
        filtersPanel = JPanel()
        filtersPanel.layout = BoxLayout(filtersPanel, Y_AXIS)
        filtersPanel.border = createTitledBorder(createEtchedBorder(), "Filter Criteria (none=show all)")
        filtersPanel.minimumSize = Dimension(600, 600)
        filtersPanel.preferredSize = Dimension(600, 600)

        val rightPanel = JPanel()
        rightPanel.layout = BorderLayout(5, 5)
        val filterDirectionPanel = JPanel()
        rightPanel.add(filterDirectionPanel, NORTH)

        filterDirectionPanel.layout = BoxLayout(filterDirectionPanel, Y_AXIS)
        filterDirectionPanel.border = createTitledBorder(createEtchedBorder(), "Filter Direction (none = both)")
        filterUpCheckBox = JCheckBox()
        filterUpCheckBox.text = "Filter up on selected"
        filterDirectionPanel.add(filterUpCheckBox)
        filterDownCheckBox = JCheckBox()
        filterDownCheckBox.text = "Filter down on selected"
        filterDirectionPanel.add(filterDownCheckBox)

        val middleCheckboxPanel = JPanel()

        middleCheckboxPanel.layout = BoxLayout(middleCheckboxPanel, Y_AXIS)
        rightPanel.add(middleCheckboxPanel, CENTER)


        rankingCheckBox = JCheckBox()
        rankingCheckBox.isSelected = false
        rankingCheckBox.text = "Use rankings"
        rankingCheckBox.toolTipText = "This groups the graph into horizontal layers, where all peers are in the same  layer"
        middleCheckboxPanel.add(rankingCheckBox)

        familyTreeCheckBox = JCheckBox()
        familyTreeCheckBox.text = "Family Tree"
        familyTreeCheckBox.toolTipText = "is this a family tree, or a data graph?  Family trees can include concepts like spouses, births, deaths, etc."
        middleCheckboxPanel.add(familyTreeCheckBox)

        clearFiltersButton = JButton()
        clearFiltersButton.text = "Clear all selections"
        middleCheckboxPanel.add(clearFiltersButton)

        val bottomPanel = JPanel()

        bottomPanel.layout = BoxLayout(bottomPanel, X_AXIS)
        rightPanel.add(bottomPanel, SOUTH)

        val leftButtonPanel = JPanel()
        leftButtonPanel.layout = BoxLayout(leftButtonPanel, Y_AXIS)
        val rightButtonPanel = JPanel()
        rightButtonPanel.layout = BoxLayout(rightButtonPanel, Y_AXIS)
        bottomPanel.add(leftButtonPanel)
        bottomPanel.add(rightButtonPanel)

        loadDatafileButton = JButton()
        loadDatafileButton.text = "Load Data file"
        loadDatafileButton.toolTipText = "Load a data set from file on disk"
        leftButtonPanel.add(loadDatafileButton)

        editDataButton = JButton()
        editDataButton.text = "Edit/Add Data"
        editDataButton.toolTipText = "Edit or add to an existing data file"
        leftButtonPanel.add(editDataButton)

        saveDataFileButton = JButton()
        saveDataFileButton.text = "Save Data file"
        saveDataFileButton.toolTipText = "Save open dataset to disk"
        leftButtonPanel.add(saveDataFileButton)

        newDataButton = JButton()
        newDataButton.text = "New Data file"
        newDataButton.toolTipText = "Create a new data file"
        leftButtonPanel.add(newDataButton)

        findDotButton = JButton()
        findDotButton.text = "Find Dot"
        findDotButton.toolTipText = "If Dot is in a non-standard location, click this to get a dialog to find it"
        rightButtonPanel.add(findDotButton)

        makeGraphButton = JButton()
        makeGraphButton.text = "Make Graph"
        rightButtonPanel.add(makeGraphButton)

        quitButton = JButton()
        quitButton.text = "Quit"
        rightButtonPanel.add(quitButton)

        contentPane.add(filtersPanel, CENTER)
        contentPane.add(rightPanel, EAST)

    }

    companion object {
        private const val LAST_DIR: String = "LAST_DIR"
        private val logger: Logger = LoggerFactory.getLogger(LoadersUi::class.java)
        private const val DOT_EXECUTABLE = "dotExecutable"
        private const val USE_RANKING = "userRanking"
        private const val FILTER_UP = "filterUp"
        private const val FILTER_DOWN = "filterDown"
        private const val FAMILY_TREE = "familyTree"
        private const val WINDOWS_DOT_LOCATION = "\"C:\\Program Files\\ATT\\Graphviz\\bin\\dot.exe\""
        private const val OSX_DOT_LOCATION = "/Applications/Graphviz.app/Contents/MacOS/dot"
        private const val OSX_HOMEBREW_DOT_LOCATION = "/opt/homebrew/bin/dot"
        private const val MAC_OS = "Mac OS"
        private const val PREVIEW_LOCATION = "/Applications/Preview.app/Contents/MacOS/Preview"
        private const val WINDOWS = "windows"

        @JvmStatic
        fun main(args: Array<String>) { //            val ui = LoadersUi()
            SwingUtilities.invokeLater {
                val loadersUi = LoadersUi()
                loadersUi.loadDatafile(File("src/test/resources/data/family_tree.json"))
            }
        }
    }
}

