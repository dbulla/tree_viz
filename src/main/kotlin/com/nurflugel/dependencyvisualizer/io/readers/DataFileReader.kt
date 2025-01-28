package com.nurflugel.dependencyvisualizer.io.readers

import com.nurflugel.dependencyvisualizer.Constants.Companion.COLOR
import com.nurflugel.dependencyvisualizer.Constants.Companion.NAME
import com.nurflugel.dependencyvisualizer.Constants.Companion.SHAPE
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.Person
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
import java.nio.charset.Charset
import java.util.*

/**
 * Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:24:16 PM To change this template use File | Settings | File Templates.
 */
abstract class DataFileReader {
    lateinit var sourceDataFile: File
    lateinit var fileType: FileType

    protected constructor(sourceDataFile: File) {
        this.sourceDataFile = sourceDataFile
    }

    constructor()

    /**
     * Parse the individual line.
     */
    private fun parseObjectDeclaration(line: String, ranking: Ranking, dataSet: BaseDependencyDataSet) {
        val lineText = line.trim { it <= ' ' }
        val strings = lineText
            .split("""\|""".toRegex())
            .dropLastWhile { it.isEmpty() }
            .toTypedArray()
        val dependencyObject: BaseDependencyObject
        val name = strings[0]

        dependencyObject = when {
            dataSet.isFamilyTree -> Person(name, ranking.name)
            else                 -> DependencyObject(name, ranking.name)
        }

        if (strings.size > 1) {
            dependencyObject.displayName = strings[1].trim { it <= ' ' }
        }

        if (strings.size == 3) {
            val notes = strings[2]
                .split("`".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toMutableList()

            dependencyObject.notes = notes
        }

        dataSet.add(dependencyObject)
    }

    // -------------------------- OTHER METHODS --------------------------
    fun readObjectsFromFile(): BaseDependencyDataSet {
        val dataSet = parseLines()

        dataSet.generateRankingsMap()

        return dataSet
    }

    protected open fun parseLines(): BaseDependencyDataSet {
        val lines: MutableList<String>
        val isFamilyTree: Boolean
        var dataSet: BaseDependencyDataSet = DependencyDataSet()

        try {
            lines = FileUtils.readLines(sourceDataFile, Charset.defaultCharset())
            isFamilyTree = lines
                .filter { it.startsWith("&") }
                .any { this.isFamilyHistory(it) }
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
            val lineToParse = line.trim { it <= ' ' }
            val chunks = lineToParse
                .substring(0, lineToParse.length)  // wtf???
                .split("->".toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray()

            for (i in 0..<(chunks.size - 1)) {
                val main = dataSet.objectByName(chunks[i])
                val dependency = dataSet.objectByName(chunks[i + 1])

                main.addDependency(dependency.name)
            }
        } catch (e: Exception) {
            LOGGER.error("Error parsing dependency line: $line", e)
            throw e
        }
    }

    private fun getObjectType(line: String): Ranking {
        val strippedLine = line.substring(line.indexOf('#') + 1).trim { it <= ' ' }
        val chunks = strippedLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var shape: Shape = Shape.RECTANGLE
        var color: Color = Color.BLACK
        var name = "noName"

        for (chunk in chunks) {
            val nibbles = chunk.trim { it <= ' ' }.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val trimmedNibble = nibbles[0].trim { it <= ' ' }
            when {
                trimmedNibble.equals(NAME, ignoreCase = true)  -> name = nibbles[1].trim { it <= ' ' }
                trimmedNibble.equals(COLOR, ignoreCase = true) -> color = Color.valueOf(nibbles[1].trim { it <= ' ' }.uppercase(Locale.getDefault()))
                trimmedNibble.equals(SHAPE, ignoreCase = true) -> shape = Shape.valueOf(nibbles[1].trim { it <= ' ' }.uppercase(Locale.getDefault()))
            }
        }

        return Ranking.valueOf(name, color, shape)
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

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DataFileReader::class.java)
    }
}
