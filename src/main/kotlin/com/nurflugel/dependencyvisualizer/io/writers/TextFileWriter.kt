package com.nurflugel.dependencyvisualizer.io.writers

import com.nurflugel.dependencyvisualizer.data.DataHandler
import java.io.File

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:46:04 PM To change this template use File | Settings | File Templates.  */
class TextFileWriter(sourceDataFile: File) : DataFileWriter(sourceDataFile) {
    override fun saveToFile(dataHandler: DataHandler) {}
}
