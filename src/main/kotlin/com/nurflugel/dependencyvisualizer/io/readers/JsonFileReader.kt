package com.nurflugel.dependencyvisualizer.io.readers

import com.google.gson.GsonBuilder
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet
import org.apache.commons.io.FileUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Type

class JsonFileReader : DataFileReader {
    internal constructor(sourceDataFile: File) : super(sourceDataFile)

    constructor()

    override fun parseLines(): BaseDependencyDataSet {
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()

        // determine if it's a family tree BEFORE determining which type of data set
        var lines: List<String>

        try {
            lines = FileUtils.readLines(sourceDataFile)
        } catch (e: IOException) {
            e.printStackTrace()
            lines = ArrayList()
        }

        val isFamilyTree = lines.stream()
            .filter { l: String -> l.contains("isFamilyTree") }
            .anyMatch { l: String -> l.contains("true") }

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

    override fun equals(o: Any?): Boolean {
        if (o === this) return true
        if (o !is JsonFileReader) return false
        if (!o.canEqual(this as Any)) return false
        return true
    }

override     fun canEqual(other: Any?): Boolean {
        return other is JsonFileReader
    }

    override fun hashCode(): Int {
        val result = 1
        return result
    }

    override fun toString(): String {
        return "JsonFileReader()"
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(JsonFileReader::class.java)
    }
}
