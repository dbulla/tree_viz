package com.nurflugel.dependencyvisualizer.io.readers

import com.google.gson.GsonBuilder
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet
import org.apache.commons.io.FileUtils
import java.io.BufferedReader
import java.io.FileReader
import java.io.IOException
import java.lang.reflect.Type
import java.nio.charset.Charset

class JsonFileReader : DataFileReader() {

    override fun parseLines(): BaseDependencyDataSet {
        val gsonBuilder = GsonBuilder()
        val gson = gsonBuilder.create()

        // determine if it's a family tree BEFORE determining which type of data set
        val lines: List<String> = try {
            FileUtils.readLines(sourceDataFile, Charset.defaultCharset())
        } catch (e: IOException) {
            e.printStackTrace()
            ArrayList()
        }

        // First, we need to determine which class to use - a Family Tree, or a generic object?
        val isFamilyTree = lines
            .filter { it.contains("isFamilyTree") }
            .any { it.contains("true") }

        try {
            BufferedReader(FileReader(sourceDataFile)).use { br ->
                // convert the json string back to object
                val dataSet: BaseDependencyDataSet
                val theClazz: Type = when {
                    isFamilyTree -> FamilyTreeDataSet::class.java
                    else         -> DependencyDataSet::class.java
                }

                dataSet = gson.fromJson(br, theClazz)
                dataSet.rectify()
                return dataSet
            }
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }
}
