package com.nurflugel.dependencyvisualizer.io.writers;

import com.nurflugel.dependencyvisualizer.data.DataHandler;
import com.nurflugel.dependencyvisualizer.enums.FileType;
import java.io.File;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:45:53 PM To change this template use File | Settings | File Templates. */
public abstract class DataFileWriter
{
  protected File  sourceDataFile;
  public FileType fileType;

  protected DataFileWriter(File sourceDataFile)
  {
    this.sourceDataFile = sourceDataFile;
  }

  public abstract void saveToFile(DataHandler dataHandler);
}
