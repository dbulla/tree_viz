package com.nurflugel.dependencyvisualizer.data

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject.Companion.replaceAllBadChars
import com.nurflugel.dependencyvisualizer.data.pojos.Person
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter
import com.nurflugel.dependencyvisualizer.enums.Ranking
import com.nurflugel.dependencyvisualizer.io.DataFileFactory
import com.nurflugel.dependencyvisualizer.io.readers.DataFileReader
import com.nurflugel.dependencyvisualizer.io.writers.DataFileWriter
import com.nurflugel.dependencyvisualizer.io.writers.DotFileWriter
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.util.*

/**
 * Class to read in the data file and process it.
 */
class DataHandler(sourceDataFile: File) {
    private val dotFile: File
    private var directionalFilters: MutableList<DirectionalFilter> = mutableListOf()
    private var typesFilters: MutableList<Ranking> = mutableListOf()
    private var keyObjects: MutableSet<BaseDependencyObject> = mutableSetOf()
    private var isRanking = true
    private lateinit var dataFileReader: DataFileReader
    private lateinit var dataFileWriter: DataFileWriter
    lateinit var dataset: BaseDependencyDataSet

    init {
        val dataFileFactory = DataFileFactory(sourceDataFile)
        val dotPath = dataFileFactory.dotPath

        try {
            dataFileReader = dataFileFactory.reader
            dataFileWriter = dataFileFactory.writer
        } catch (e: Exception) {
            e.printStackTrace()
        }

        dotFile = File(dotPath)
    }

    // -------------------------- OTHER METHODS --------------------------
    fun writeObjects(): File {
        val filteredObjects = filterObjects()
        val writer = DotFileWriter(dotFile, isRanking)

        try {
            writer.writeObjectsToDotFile(filteredObjects)
        } catch (e: Exception) {
            LOGGER.error("Error writing file", e)
        }

        return dotFile
    }

    /**
     * Filter the objects based on criteria from the UI- - Specific objects - filter up or down - show/don't show tiers (no NSC, etc.).
     */
    private fun filterObjects(): Collection<BaseDependencyObject> {
        val filter = ObjectFilterer(directionalFilters, typesFilters)

        return filter.filter(dataset, keyObjects)
    }

    /**
     * See if the given object is found. if not, throw an exception.
     */
    @Throws(Exception::class)
    fun findObjectByName(name: String): BaseDependencyObject {
        val cleanName = replaceAllBadChars(name)
        val baseDependencyObject = dataset.objectByName(cleanName)
        return baseDependencyObject
    }

    fun loadDataset() {
        dataset = dataFileReader.readObjectsFromFile()
    }

    fun initialize() {
        directionalFilters = ArrayList()
        typesFilters = ArrayList()
        keyObjects = TreeSet()
    }

    // todo - do I really need these?
    fun setDirectionalFilters(directionalFilters: List<DirectionalFilter>) {
        this.directionalFilters = ArrayList(directionalFilters)
    }

    fun setKeyObjectsToFilterOn(keyObjects: List<BaseDependencyObject>) {
        this.keyObjects = TreeSet(keyObjects)

        if (dataset is FamilyTreeDataSet) {
            keyObjects
                .filterIsInstance<Person>()
                .forEach { person ->
                    (dataset as FamilyTreeDataSet).getMarriagesForPerson(person)
                        .map { it.getSpouse(person.name) }
                        .forEach { this.keyObjects.add(dataset.objectByName(it)) }
                }
        }
    }

    fun setTypesFilters(typesFilters: List<Ranking>) {
        this.typesFilters = ArrayList(typesFilters)
    }

    fun saveDataset() {
        dataFileWriter.saveToFile(this)
    }

    fun setRanking(isRanking: Boolean) {
        this.isRanking = isRanking
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DataHandler::class.java)
    }
}
