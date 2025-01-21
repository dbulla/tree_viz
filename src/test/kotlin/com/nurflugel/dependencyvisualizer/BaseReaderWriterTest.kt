package com.nurflugel.dependencyvisualizer

import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.nio.charset.Charset

open class BaseReaderWriterTest {

    protected fun doTestComparisons(sourceDataFile: File, expectedDotFile: File) {
        val resultFile = File(StringUtils.replace(sourceDataFile.absolutePath, ".txt", ".dot"))
        val testOutput = getExpectedOutput(resultFile)
        val expectedOutput = getExpectedOutput(expectedDotFile)

        logger.info("Comparing $expectedDotFile and $resultFile")

        //    assertEquals(resultFile + " and " + expectedDotFile + " should have the same number of lines", expectedOutput.length, testOutput.length);
        expectedOutput.indices.forEach { i ->
            assertEquals(expectedOutput[i], testOutput[i], "Test output at line $i should be equal to expected output");
        }
    }

    /**
     * Gets the expected output from the .dot file.
     *
     * @param   dotFile  The .dot file to read
     *
     * @return  A string array, where each element represents one line of the.dot text file.
     */
    private fun getExpectedOutput(dotFile: File): Array<String> {
        val strings = FileUtils.readLines(dotFile, Charset.defaultCharset())

        return strings.toTypedArray<String>()
    }

    companion object {
        val logger: Logger = LoggerFactory.getLogger(BaseReaderWriterTest::class.java)
    }
}
