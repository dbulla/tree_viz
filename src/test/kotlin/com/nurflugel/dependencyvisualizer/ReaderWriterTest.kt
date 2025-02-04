package com.nurflugel.dependencyvisualizer

import com.nurflugel.dependencyvisualizer.data.DataHandler
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject.Companion.replaceAllBadChars
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.DOWN
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.UP
import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.first
import org.apache.commons.io.FileUtils
import org.junit.jupiter.api.Test
import java.io.File

class ReaderWriterTest : BaseReaderWriterTest() {

    @Test
    fun testAutosys() {
        val sourceDataFile = File("build/resources/test/data/Autosys_dependencies.json")
        val generatedDotFile = File("build/resources/test/data/Autosys_dependencies.dot")
        val expectedDotFile = File("build/resources/test/data/Autosys_dependencies_saved.dot")
        val dataHandler = DataHandler(sourceDataFile)

        dataHandler.loadDataset()
        dataHandler.writeObjects()
        doTestComparisons(generatedDotFile, expectedDotFile)
    }

    @Test
    fun testNoFilters() {
        val sourceDataFile = File("build/resources/test/data/test_dependencies.txt")
        val expectedDotFile = File("build/resources/test/data/test_dependencies_saved.dot")
        val directionalFilters: List<DirectionalFilter> = ArrayList()
        val keyObjects: List<BaseDependencyObject> = ArrayList()
        val dataHandler = DataHandler(sourceDataFile)
        dataHandler.loadDataset()
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.loadDataset()
        dataHandler.writeObjects()
        doTestComparisons(sourceDataFile, expectedDotFile)
    }

    @Test
    fun testUpFilters() {
        val destFile = File("build/resources/test/data/test_filters_up.txt")

        FileUtils.copyFile(File("build/resources/test/data/test_dependencies.txt"), destFile)

        val expectedDotFile = File("build/resources/test/data/test_filters_up_saved.dot")
        val dataHandler = DataHandler(destFile)
        val directionalFilters: MutableList<DirectionalFilter> = ArrayList()
        val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

        directionalFilters.add(UP)
        dataHandler.loadDataset()

        val itemD = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D))

        keyObjects.add(itemD)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.writeObjects()
        doTestComparisons(destFile, expectedDotFile)
    }

    @Test
    fun testUpFiltersOnCdm() {
        val destFile = File("build/resources/test/data/test_filters_up_cdm.txt")

        FileUtils.copyFile(File("build/resources/test/data/Autosys_dependencies.txt"), destFile)

        val expectedDotFile = File("build/resources/test/data/test_filters_up_cdm_saved.dot")
        val dataHandler = DataHandler(destFile)
        val directionalFilters: MutableList<DirectionalFilter> = ArrayList()
        val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

        directionalFilters.add(UP)
        dataHandler.loadDataset()

        val styleObject: BaseDependencyObject = DependencyObject("cdm_style", first().name)

        keyObjects.add(styleObject)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.writeObjects()
        doTestComparisons(destFile, expectedDotFile)
    }

    @Test
    fun testDownFilters() {
        val destFile = File("build/resources/test/data/test_filters_down.txt")

        FileUtils.copyFile(File("build/resources/test/data/test_dependencies.txt"), destFile)

        val expectedDotFile = File("build/resources/test/data/test_filters_down_saved.dot")
        val dataHandler = DataHandler(destFile)
        val directionalFilters: MutableList<DirectionalFilter> = ArrayList()
        val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

        directionalFilters.add(DOWN)
        dataHandler.loadDataset()

        val itemD = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D))

        keyObjects.add(itemD)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.writeObjects()
        doTestComparisons(destFile, expectedDotFile)
    }

    @Test
    fun testUpAndDownFilters() {
        val destFile = File("build/resources/test/data/test_filters_up_and_down.txt")

        FileUtils.copyFile(File("build/resources/test/data/test_dependencies.txt"), destFile)

        val expectedDotFile = File("build/resources/test/data/test_filters_up_and_down_saved.dot")
        val dataHandler = DataHandler(destFile)
        val directionalFilters: MutableList<DirectionalFilter> = ArrayList()
        val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

        directionalFilters.add(DOWN)
        directionalFilters.add(UP)
        dataHandler.loadDataset()

        val itemD = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D))

        keyObjects.add(itemD)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.writeObjects()
        doTestComparisons(destFile, expectedDotFile)
    }


    companion object {
        const val ITEM_D: String = "Item d"
    }
}
