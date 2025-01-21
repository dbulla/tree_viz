package com.nurflugel.dependencyvisualizer.io.readers

import com.google.gson.GsonBuilder
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset

class JsonFileReader : DataFileReader {
    internal constructor(sourceDataFile: File) : super(sourceDataFile)

    constructor()

    override fun parseLines(): BaseDependencyDataSet {
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()

        // determine if it's a family tree BEFORE determining which type of data set
        var lines: List<String>

        try {
            lines = FileUtils.readLines(sourceDataFile, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
            lines = ArrayList()
        }

        // First, we need to determine which class to use - a Family Tree, or a generic object?
        val isFamilyTree = lines
            .filter { it.contains("isFamilyTree") }
            .any { it.contains("true") }

        try {
            BufferedReader(FileReader(sourceDataFile)).use { br ->
                // convert the json string back to object
                val dataSet: BaseDependencyDataSet
                val theClazz: Type = if (isFamilyTree)
                    FamilyTreeDataSet::class.java
                else
                    DependencyDataSet::class.java

                dataSet = gson.fromJson(br, theClazz)
                dataSet.rectify()
                println("dataSet = $dataSet")
                return dataSet
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

//    override fun equals(other: Any?): Boolean {
//        if (other === this) return true
//        if (other !is JsonFileReader) return false
//        if (!other.canEqual(this as Any)) return false
//        return true
//    }
//
//override     fun canEqual(other: Any?): Boolean {
//        return other is JsonFileReader
//    }
//
//    override fun hashCode(): Int {
//        val result = 1
//        return result
//    }
//
//    override fun toString(): String {
//        return "JsonFileReader()"
//    }
//
//    companion object {
//        val LOGGER: Logger = LoggerFactory.getLogger(JsonFileReader::class.java)
//    }
}
