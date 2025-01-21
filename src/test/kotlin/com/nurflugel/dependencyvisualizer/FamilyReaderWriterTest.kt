package com.nurflugel.dependencyvisualizer

import com.nurflugel.dependencyvisualizer.data.DataHandler
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.DOWN
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.UP
import org.junit.jupiter.api.Test
import java.io.File

class FamilyReaderWriterTest : BaseReaderWriterTest() {
    private val destFile = File("build/resources/test/data/family_tree.dot")
    private val sourceDataFile = File("build/resources/test/data/family_tree.json")

    @Test
    fun testUpFiltersOnDoug() {
        val expectedDotFile = File("build/resources/test/data/family_tree.dot")
        val dataHandler = DataHandler(sourceDataFile)
        val directionalFilters: MutableList<DirectionalFilter> = mutableListOf()
        val keyObjects: MutableList<BaseDependencyObject> = mutableListOf()

        directionalFilters.add(UP)
        dataHandler.loadDataset()

        val douglas = dataHandler.findObjectByName("douglas_bullard")

        keyObjects.add(douglas)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.doIt()
        doTestComparisons(destFile, expectedDotFile)
    }

   @Test
    fun testDownFiltersOnWylie() {
        val expectedDotFile = File("build/resources/test/data/family_tree_DownFiltersOnWylie_saved.dot")

        val dataHandler = DataHandler(sourceDataFile)
        val directionalFilters: MutableList<DirectionalFilter> = mutableListOf()
        val keyObjects: MutableList<BaseDependencyObject> = mutableListOf()

        directionalFilters.add(DOWN)
        dataHandler.loadDataset()

        val wylie = dataHandler.findObjectByName("Wylie_Aubrey_Bullard")

        keyObjects.add(wylie)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.doIt()
        doTestComparisons(destFile, expectedDotFile)
    }

   @Test
    fun testUpDownFiltersOnWylie() {

        val expectedDotFile = File("build/resources/test/data/family_tree_UpDownFiltersOnWylie_saved.dot")

        val dataHandler = DataHandler(sourceDataFile)
        val directionalFilters: MutableList<DirectionalFilter> = mutableListOf()
        val keyObjects: MutableList<BaseDependencyObject> = mutableListOf()

        directionalFilters.add(UP)
        directionalFilters.add(DOWN)
        dataHandler.loadDataset()

        val wylie = dataHandler.findObjectByName("Wylie_Aubrey_Bullard")

        keyObjects.add(wylie)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.doIt()
        doTestComparisons(destFile, expectedDotFile)
    }


  @Test
    fun testAllFamily() {

        val expectedDotFile = File("build/resources/test/data/family_tree_AllFamily_saved.dot")

        val dataHandler = DataHandler(sourceDataFile)
//        val directionalFilters: MutableList<DirectionalFilter> = mutableListOf()
//        val keyObjects: MutableList<BaseDependencyObject> = mutableListOf()

        dataHandler.loadDataset()

//        val wylie = dataHandler.findObjectByName("Wylie_Aubrey_Bullard")
//
//        keyObjects.add(wylie)
//        dataHandler.setKeyObjectsToFilterOn(keyObjects)
//        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.doIt()
        doTestComparisons(destFile, expectedDotFile)
    }



}
