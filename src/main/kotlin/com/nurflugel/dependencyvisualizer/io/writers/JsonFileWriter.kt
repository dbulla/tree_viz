package com.nurflugel.dependencyvisualizer.io.writers

import com.google.gson.GsonBuilder
import com.nurflugel.dependencyvisualizer.data.DataHandler
import com.nurflugel.dependencyvisualizer.enums.FileType.JSON
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileWriter
import java.io.IOException

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:46:12 PM To change this template use File | Settings | File Templates.  */
class JsonFileWriter(sourceDataFile: File) : DataFileWriter(sourceDataFile) {
    init {
        fileType = JSON
    }

    override fun saveToFile(dataHandler: DataHandler) {
        val dataSet = dataHandler.dataset

        dataSet.generateRankingsMap()

        var fileName = super.sourceDataFile.absolutePath
        val baseName = FilenameUtils.getBaseName(fileName)

        fileName = baseName + fileType.getExtension()

        val gson = GsonBuilder().setPrettyPrinting()

        // convert java object to JSON format,
        // and returned as a JSON formatted string
        val json = gson.create().toJson(dataSet)

        val file = File(sourceDataFile.parent, fileName)

        try {
            FileWriter(file).use { writer ->
                // write converted json data to a file named "file.json"
                writer.write(json)
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}
