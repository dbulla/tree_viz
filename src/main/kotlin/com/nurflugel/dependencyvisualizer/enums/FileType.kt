package com.nurflugel.dependencyvisualizer.enums

import com.nurflugel.dependencyvisualizer.io.readers.DataFileReader
import com.nurflugel.dependencyvisualizer.io.readers.JsonFileReader
import com.nurflugel.dependencyvisualizer.io.readers.TextFileReader
import com.nurflugel.dependencyvisualizer.io.writers.DataFileWriter
import com.nurflugel.dependencyvisualizer.io.writers.JsonFileWriter
import com.nurflugel.dependencyvisualizer.io.writers.TextFileWriter
import org.apache.commons.lang3.StringUtils
import java.io.File
import java.util.stream.Stream

/** What type of text file is this?  */
enum class FileType(private val extension: String, private val fileReaderClass: Class<out DataFileReader?>, private val fileWriterClass: Class<out DataFileWriter?>) {
    JSON(".json", JsonFileReader::class.java, JsonFileWriter::class.java),
    TXT(".txt", TextFileReader::class.java, TextFileWriter::class.java);

    fun getExtension(): String { return extension}
    fun getDotPath(absolutePath: String?): String {
        return StringUtils.replace(absolutePath, extension, ".dot")
    }

    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun getDataFileReader(sourceDataFile: File): DataFileReader {
        val reader = fileReaderClass.newInstance()!!

        reader.sourceDataFile = sourceDataFile

        return reader
    }

    @Throws(IllegalAccessException::class, InstantiationException::class)
    fun getDataFileWriter(sourceDataFile: File): DataFileWriter {
        val reader = fileWriterClass.newInstance()!!

        return reader
    }

    companion object {
        fun findByExtension(text: String): FileType {
            val fullText = ".$text"

            return Stream.of<FileType>(*entries.toTypedArray())
                .filter { v: FileType -> v.extension == fullText }
                .findAny().orElseThrow { RuntimeException() }
        }
    }
}
