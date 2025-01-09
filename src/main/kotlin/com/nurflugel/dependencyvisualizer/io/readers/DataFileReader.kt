package com.nurflugel.dependencyvisualizer.io.readers

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.Person
import com.nurflugel.dependencyvisualizer.enums.FileType
import com.nurflugel.dependencyvisualizer.enums.Ranking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

/**
 * Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:24:16 PM To change this template use File | Settings | File Templates.
 */
abstract class DataFileReader {
    lateinit var sourceDataFile: File
    var fileType: FileType? = null
    var logger: Logger = LoggerFactory.getLogger(DataFileReader::class.java)

    protected constructor(sourceDataFile: File) {
        this.sourceDataFile = sourceDataFile
    }

    constructor()

    /**
     * Parse the individual line.
     */
    protected fun parseObjectDeclaration(line: String, ranking: Ranking, dataSet: BaseDependencyDataSet) {
        val lineText = line.trim { it <= ' ' }
        val strings = lineText.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
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
            val notes = strings[2].split("`".toRegex()).dropLastWhile { it.isEmpty() }.toMutableList()

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

    protected abstract fun parseLines(): BaseDependencyDataSet

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is DataFileReader) return false
        val other = o
        if (!other.canEqual(this as Any)) return false
        val `this$sourceDataFile`: Any? = this.sourceDataFile
        val `other$sourceDataFile`: Any? = other.sourceDataFile
        if (if (`this$sourceDataFile` == null) `other$sourceDataFile` != null else (`this$sourceDataFile` != `other$sourceDataFile`)) return false
        val `this$fileType`: Any? = this.fileType
        val `other$fileType`: Any? = other.fileType
        if (if (`this$fileType` == null) `other$fileType` != null else (`this$fileType` != `other$fileType`)) return false
        val `this$logger`: Any = this.logger
        val `other$logger`: Any = other.logger
        return `this$logger` == `other$logger`
    }

    protected open fun canEqual(other: Any?): Boolean {
        return other is DataFileReader
    }

    override fun hashCode(): Int {
        val PRIME = 59
        var result = 1
        val `$sourceDataFile`: Any? = this.sourceDataFile
        result = result * PRIME + (`$sourceDataFile`?.hashCode()
                                   ?: 43)
        val `$fileType`: Any? = this.fileType
        result = result * PRIME + (`$fileType`?.hashCode()
                                   ?: 43)
        val `$logger`: Any = this.logger
        result = result * PRIME + (`$logger`?.hashCode()
                                   ?: 43)
        return result
    }

    override fun toString(): String {
        return "DataFileReader(sourceDataFile=" + this.sourceDataFile + ", fileType=" + this.fileType + ", logger=" + this.logger + ")"
    }
}
