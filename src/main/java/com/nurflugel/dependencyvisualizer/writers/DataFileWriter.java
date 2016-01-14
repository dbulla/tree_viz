package com.nurflugel.dependencyvisualizer.writers;

import com.nurflugel.dependencyvisualizer.DataHandler;
import java.io.File;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:45:53 PM To change this template use File | Settings | File Templates. */
public abstract class DataFileWriter
{
  protected File sourceDataFile;

  protected DataFileWriter(File sourceDataFile)
  {
    this.sourceDataFile = sourceDataFile;
  }

  public abstract void saveToFile(DataHandler dataHandler);
}
