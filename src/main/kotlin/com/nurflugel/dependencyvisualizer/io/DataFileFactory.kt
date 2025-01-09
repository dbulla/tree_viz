package com.nurflugel.dependencyvisualizer.io

import com.nurflugel.dependencyvisualizer.enums.FileType.Companion.findByExtension
import com.nurflugel.dependencyvisualizer.io.readers.DataFileReader
import com.nurflugel.dependencyvisualizer.io.writers.DataFileWriter
import com.nurflugel.dependencyvisualizer.io.writers.JsonFileWriter
import org.apache.commons.io.FilenameUtils
import java.io.File

/** simple factory to get the right stuff for the different types of data files.  */
class DataFileFactory(private val sourceDataFile: File) {
    private val fileType =
        findByExtension(FilenameUtils.getExtension(sourceDataFile.absolutePath))

//    @get:Throws(InstantiationException::class, IllegalAccessException::class)
    val reader: DataFileReader
        get() = fileType.getDataFileReader(sourceDataFile)

//    @get:Throws(InstantiationException::class, IllegalAccessException::class)
    val writer: DataFileWriter
        /** for right now, always return an XML writer.  */
        get() = // return fileType.getDataFileWriter(sourceDataFile);
            JsonFileWriter(sourceDataFile)

    val dotPath: String
        get() = fileType.getDotPath(sourceDataFile.absolutePath)
}
