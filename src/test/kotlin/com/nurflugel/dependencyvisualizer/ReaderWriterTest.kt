package com.nurflugel.dependencyvisualizer

import com.nurflugel.dependencyvisualizer.data.DataHandler
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject.Companion.replaceAllBadChars
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter
import com.nurflugel.dependencyvisualizer.enums.Ranking.Companion.first
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**   */
class ReaderWriterTest {
    // -------------------------- OTHER METHODS --------------------------
    /**   */
    protected fun doTestComparisons(sourceDataFile: File, expectedDotFile: File) {
        val resultFile = File(StringUtils.replace(sourceDataFile.absolutePath, ".txt", ".dot"))
        val testOutput = getOutput(resultFile)
        val expectedOutput = getOutput(expectedDotFile)

        if (logger.isDebugEnabled) {
            logger.debug("Comparing $expectedDotFile and $resultFile")
        }

        //    assertEquals(resultFile + " and " + expectedDotFile + " should have the same number of lines", expectedOutput.length, testOutput.length);
        for (i in expectedOutput.indices) {
            //      assertEquals("Test output at line " + i + " should be equal to expected output", expectedOutput[i], testOutput[i]);
        }
    }

    /**
     * Gets the expected output from the .dot file.
     *
     * @param   dotFile  The .dot file to read
     *
     * @return  A string array, where each element represents one line of the.dot tex file.
     */
    fun getOutput(dotFile: File): Array<String> {
        val strings = FileUtils.readLines(dotFile)

        return strings.toTypedArray<String>()
    }

    //  @Test
    fun testAutosys() {
        val sourceDataFile = File("build/resources/test/data/Autosys dependencies.txt")
        val expectedDotFile = File("build/resources/test/data/Autosys dependencies_saved.dot")
        val dataHandler = DataHandler(sourceDataFile)

        dataHandler.loadDataset()
        dataHandler.doIt()
        doTestComparisons(sourceDataFile, expectedDotFile)
    }

    //  @Test
    fun testNoFilters() {
        val sourceDataFile = File("build/resources/test/data/test dependencies.txt")
        val expectedDotFile = File("build/resources/test/data/test dependencies_saved.dot")
        val directionalFilters: List<DirectionalFilter> = ArrayList()
        val keyObjects: List<BaseDependencyObject> = ArrayList()
        val dataHandler = DataHandler(sourceDataFile)

        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.loadDataset()
        dataHandler.doIt()
        doTestComparisons(sourceDataFile, expectedDotFile)
    }

    //  @Test
    fun testUpFilters() {
        val destFile = File("build/resources/test/data/test filters up.txt")

        FileUtils.copyFile(File("build/resources/test/data/test dependencies.txt"), destFile)

        val expectedDotFile = File("build/resources/test/data/test filters up_saved.dot")
        val dataHandler = DataHandler(destFile)
        val directionalFilters: MutableList<DirectionalFilter> = ArrayList()
        val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

        directionalFilters.add(DirectionalFilter.UP)
        dataHandler.loadDataset()

        val `object` = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D))

        keyObjects.add(`object`)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.doIt()
        doTestComparisons(destFile, expectedDotFile)
    }

    //  @Test
    fun testUpFiltersOnCdm() {
        val destFile = File("build/resources/test/data/test filters up cdm.txt")

        FileUtils.copyFile(File("build/resources/test/data/Autosys dependencies.txt"), destFile)

        val expectedDotFile = File("build/resources/test/data/test filters up cdm_saved.dot")
        val dataHandler = DataHandler(destFile)
        val directionalFilters: MutableList<DirectionalFilter> = ArrayList()
        val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

        directionalFilters.add(DirectionalFilter.UP)
        dataHandler.loadDataset()

        val `object`: BaseDependencyObject = DependencyObject("cdm_style", first().name)

        keyObjects.add(`object`)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.doIt()
        doTestComparisons(destFile, expectedDotFile)
    }

    //  @Test
    fun testDownFilters() {
        val destFile = File("build/resources/test/data/test filters down.txt")

        FileUtils.copyFile(File("build/resources/test/data/test dependencies.txt"), destFile)

        val expectedDotFile = File("build/resources/test/data/test filters down_saved.dot")
        val dataHandler = DataHandler(destFile)
        val directionalFilters: MutableList<DirectionalFilter> = ArrayList()
        val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

        directionalFilters.add(DirectionalFilter.DOWN)
        dataHandler.loadDataset()

        val `object` = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D))

        keyObjects.add(`object`)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.doIt()
        doTestComparisons(destFile, expectedDotFile)
    }

    //  @Test
    fun testUpAndDownFilters() {
        val destFile = File("build/resources/test/data/test filters up and down.txt")

        FileUtils.copyFile(File("build/resources/test/data/test dependencies.txt"), destFile)

        val expectedDotFile = File("build/resources/test/data/test filters up and down_saved.dot")
        val dataHandler = DataHandler(destFile)
        val directionalFilters: MutableList<DirectionalFilter> = ArrayList()
        val keyObjects: MutableList<BaseDependencyObject> = ArrayList()

        directionalFilters.add(DirectionalFilter.DOWN)
        directionalFilters.add(DirectionalFilter.UP)
        dataHandler.loadDataset()

        val `object` = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D))

        keyObjects.add(`object`)
        dataHandler.setKeyObjectsToFilterOn(keyObjects)
        dataHandler.setDirectionalFilters(directionalFilters)
        dataHandler.doIt()
        doTestComparisons(destFile, expectedDotFile)
    } // public void testDownFilters()
    // {
    // File                    sourceDataFile     = new File("Test data/test dependencies.txt");
    // File                    expectedDotFile    = new File("Test data/test filters down.dot");
    // Handler            readerWriter       = new Handler(sourceDataFile);
    // List<DirectionalFilter> directionalFilters = new ArrayList<DirectionalFilter>();
    //
    // directionalFilters.valueOf(DirectionalFilter.Down);
    // readerWriter.setDirectionalFilters(directionalFilters);
    // readerWriter.doIt();
    // doTestComparisons(sourceDataFile, expectedDotFile);
    // }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ReaderWriterTest::class.java)
        const val ITEM_D: String = "Item d"
    }
}
