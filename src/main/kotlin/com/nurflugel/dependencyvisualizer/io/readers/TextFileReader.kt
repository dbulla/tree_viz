package com.nurflugel.dependencyvisualizer.io.readers

import com.nurflugel.dependencyvisualizer.Constants
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet
import com.nurflugel.dependencyvisualizer.enums.Color
import com.nurflugel.dependencyvisualizer.enums.FileType
import com.nurflugel.dependencyvisualizer.enums.Ranking
import com.nurflugel.dependencyvisualizer.enums.Shape
import org.apache.commons.io.FileUtils
import org.apache.commons.lang3.StringUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException

/**
 *
 */
class TextFileReader : DataFileReader {
    // private boolean isFamilyTree;
    internal constructor(sourceDataFile: File) : super(sourceDataFile) {
        fileType = FileType.TXT
    }

    constructor()

    override fun parseLines(): BaseDependencyDataSet {
        val lines: List<String>
        val isFamilyTree: Boolean
        var dataSet: BaseDependencyDataSet = DependencyDataSet()

        try {
            lines = FileUtils.readLines(sourceDataFile)
            isFamilyTree = lines.stream()
                .filter { it.startsWith("&") }
                .anyMatch { this.isFamilyHistory(it) }
            dataSet = when {
                isFamilyTree -> FamilyTreeDataSet()
                else         -> DependencyDataSet()
            }

            lateinit var currentRanking: Ranking
            var isDependencies = false

            for (line in lines) {
                if (LOGGER.isDebugEnabled) {
                    LOGGER.debug(line)
                }

                if (!line.startsWith("//") && (!StringUtils.isEmpty(line))) {
                    when {
                        line.startsWith("#dependencies") -> isDependencies = true
                        line.startsWith("#")             -> currentRanking = getObjectType(line)
                        isDependencies                   -> parseDependency(dataSet, line)
                        else                             -> parseObjectDeclaration(line, currentRanking, dataSet)
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return dataSet
    }

    private fun parseDependency(dataSet: BaseDependencyDataSet, line: String) {
        try {
            parseDependency(line, dataSet)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Determine if this is a family history or not.
     */
    private fun isFamilyHistory(line: String): Boolean {
        val nibbles = line.trim { it <= ' ' }.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        val isFamily = nibbles[0].toBoolean()
        val isTrue = nibbles[1].toBoolean()

        return isFamily && isTrue
    }

    /**
     *
     */
    private fun getObjectType(line: String): Ranking {
        val strippedLine = line.substring(line.indexOf('#') + 1).trim { it <= ' ' }
        val chunks = strippedLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var shape: Shape? = null
        var color: Color? = null
        var name: String? = null

        for (chunk in chunks) {
            val nibbles = chunk.trim { it <= ' ' }.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            if (nibbles[0].trim { it <= ' ' }.equals(Constants.NAME, ignoreCase = true)) {
                name = nibbles[1].trim { it <= ' ' }
            }
            else if (nibbles[0].trim { it <= ' ' }.equals(Constants.COLOR, ignoreCase = true)) {
                color = Color.valueOf(nibbles[1].trim { it <= ' ' })
            }
            else if (nibbles[0].trim { it <= ' ' }.equals(Constants.SHAPE, ignoreCase = true)) {
                shape = Shape.valueOf(nibbles[1].trim { it <= ' ' })
            }
        }

        return Ranking.valueOf(name!!, color!!, shape!!)
    }

    @Throws(Exception::class)
    private fun parseDependency(line: String, dataSet: BaseDependencyDataSet) {
        try {
            val lineToParse = line.trim { it <= ' ' }
            val chunks = lineToParse.substring(0, lineToParse.length).split("->".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            for (i in 0..<(chunks.size - 1)) {
                val main = dataSet.getLoaderObjectByName(chunks[i])
                val dependency = dataSet.getLoaderObjectByName(chunks[i + 1])

                main.addDependency(dependency.name)
            }
        } catch (e: Exception) {
            LOGGER.error("Error parsing dependency line: $line", e)
            throw e
        }
    }

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is TextFileReader) return false
        if (!o.canEqual(this as Any)) return false
        return true
    }

    override fun canEqual(other: Any?): Boolean {
        return other is TextFileReader
    }

    override fun hashCode(): Int {
        val result = 1
        return result
    }

    override fun toString(): String {
        return "TextFileReader()"
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(TextFileReader::class.java)
    }
}
