package com.nurflugel.dependencyvisualizer.io.writers

import com.google.gson.GsonBuilder
import com.nurflugel.dependencyvisualizer.data.DataHandler
import com.nurflugel.dependencyvisualizer.enums.FileType
import org.apache.commons.io.FilenameUtils
import java.io.File
import java.io.FileWriter
import java.io.IOException

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:46:12 PM To change this template use File | Settings | File Templates.  */
class JsonFileWriter(sourceDataFile: File) : DataFileWriter(sourceDataFile) {
    init {
        fileType = FileType.JSON
    }

    override fun saveToFile(dataHandler: DataHandler?) {
        val dataSet = dataHandler?.dataset

        dataSet!!.generateRankingsMap()

        var fileName = super.sourceDataFile.absolutePath
        val baseName = FilenameUtils.getBaseName(fileName)

        fileName = baseName + fileType.getExtension()

        // GsonBuilder gson = new GsonBuilder();
        // gson.registerTypeAdapter(A.class, new ATypeAdapter());
        // String json = gson.create().toJson(list);
        val gson = GsonBuilder().setPrettyPrinting()

        // gson.registerTypeAdapter(DependencyObject.class, new DependencyObjectAdapter());
        // gson.registerTypeAdapter(Person.class, new PersonAdapter());
        val json = gson.create().toJson(dataSet)

        // Gson gson = new GsonBuilder().setPrettyPrinting().create();

        // convert java object to JSON format,
        // and returned as a JSON formatted string
        // String json = gson.toJson(dataSet);
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
