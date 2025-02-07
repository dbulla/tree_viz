package com.nurflugel.dependencyvisualizer.io.readers

import org.slf4j.Logger
import org.slf4j.LoggerFactory

// Read in a Graphviz .dot file
class DotFileReader : DataFileReader {
    // private boolean isFamilyTree;
//    internal constructor(sourceDataFile: File) : super(sourceDataFile) {
//        fileType = FileType.DOT
//    }

    constructor()
//
//    override fun parseLines(): BaseDependencyDataSet {
//        val lines: MutableList<String>
//        val isFamilyTree: Boolean
//        var dataSet: BaseDependencyDataSet = DependencyDataSet()
//
//        try {
//            lines = FileUtils.readLines(sourceDataFile)
//            isFamilyTree = lines
//                .filter { it.startsWith("&") }
//                .any { this.isFamilyHistory(it) }
//            dataSet = when {
//                isFamilyTree -> FamilyTreeDataSet()
//                else         -> DependencyDataSet()
//            }
//
//            lateinit var currentRanking: Ranking
//            var isDependencies = false
//
//            for (line in lines) {
//                if (LOGGER.isDebugEnabled) {
//                    LOGGER.debug(line)
//                }
//
//                if (!line.startsWith("//") && (!StringUtils.isEmpty(line))) {
//                    when {
//                        line.startsWith("#dependencies") -> isDependencies = true
//                        line.startsWith("#")             -> currentRanking = getObjectType(line)
//                        isDependencies                   -> parseDependency(dataSet, line)
//                        else                             -> parseObjectDeclaration(line, currentRanking, dataSet)
//                    }
//                }
//            }
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        return dataSet
//    }

//    private fun parseDependency(dataSet: BaseDependencyDataSet, line: String) {
//        try {
//            parseDependency(line, dataSet)
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

//    /**
//     * Determine if this is a family history or not.
//     */
//    private fun isFamilyHistory(line: String): Boolean {
//        val nibbles = line.trim { it <= ' ' }.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        val isFamily = nibbles[0].toBoolean()
//        val isTrue = nibbles[1].toBoolean()
//
//        return isFamily && isTrue
//    }

//    /**
//     *
//     */
//    private fun getObjectType(line: String): Ranking {
//        val strippedLine = line.substring(line.indexOf('#') + 1).trim { it <= ' ' }
//        val chunks = strippedLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//        var shape: Shape=Shape.rectangle
//        var color: Color=Color.black
//        var name: String="noName"
//
//        for (chunk in chunks) {
//            val nibbles = chunk.trim { it <= ' ' }.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
//
//            val trimmedNibble = nibbles[0].trim { it <= ' ' }
//            when {
//                trimmedNibble.equals(Constants.NAME, ignoreCase = true)  -> name = nibbles[1].trim { it <= ' ' }.uppercase(Locale.getDefault())
//                trimmedNibble.equals(Constants.COLOR, ignoreCase = true) -> color = Color.valueOf(nibbles[1].trim { it <= ' ' }.uppercase(Locale.getDefault()))
//                trimmedNibble.equals(Constants.SHAPE, ignoreCase = true) -> shape = Shape.valueOf(nibbles[1].trim { it <= ' ' }.uppercase(Locale.getDefault()))
//            }
//        }
//
//        return Ranking.valueOf(name, color, shape)
//    }

//    @Throws(Exception::class)
//    private fun parseDependency(line: String, dataSet: BaseDependencyDataSet) {
//        try {
//            val lineToParse = line.trim { it <= ' ' }
//            val chunks = lineToParse
//                .substring(0, lineToParse.length)  // wtf???
//                .split("->".toRegex())
//                .dropLastWhile { it.isEmpty() }
//                .toTypedArray()
//
//            for (i in 0..<(chunks.size - 1)) {
//                val main = dataSet.getLoaderObjectByName(chunks[i])
//                val dependency = dataSet.getLoaderObjectByName(chunks[i + 1])
//
//                main.addDependency(dependency.name)
//            }
//        } catch (e: Exception) {
//            LOGGER.error("Error parsing dependency line: $line", e)
//            throw e
//        }
//    }

//    override fun equals(other: Any?): Boolean {
//        if (other === this) return true
//        if (other !is TextFileReader) return false
//        if (!other.canEqual(this as Any)) return false
//        return true
//    }
//
//    override fun canEqual(other: Any?): Boolean {
//        return other is TextFileReader
//    }
//
//    override fun hashCode(): Int {
//        val result = 1
//        return result
//    }

    override fun toString(): String {
        return "TextFileReader()"
    }

    companion object {
        private val LOGGER: Logger = LoggerFactory.getLogger(DotFileReader::class.java)
    }
}
