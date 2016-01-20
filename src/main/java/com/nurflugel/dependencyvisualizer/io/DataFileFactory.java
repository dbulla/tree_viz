package com.nurflugel.dependencyvisualizer.io;

import com.nurflugel.dependencyvisualizer.enums.FileType;
import com.nurflugel.dependencyvisualizer.io.readers.DataFileReader;
import com.nurflugel.dependencyvisualizer.io.writers.DataFileWriter;
import com.nurflugel.dependencyvisualizer.io.writers.JsonFileWriter;
import java.io.File;
import static com.nurflugel.dependencyvisualizer.enums.FileType.findByExtension;
import static org.apache.commons.io.FilenameUtils.getExtension;

/** simple factory to get the right stuff for the different types of data files. */
public class DataFileFactory
{
  private FileType fileType;
  private File     sourceDataFile;

  public DataFileReader getReader() throws InstantiationException, IllegalAccessException
  {
    return fileType.getDataFileReader(sourceDataFile);
  }

  /** for right now, always return an XML writer. */
  public DataFileWriter getWriter() throws InstantiationException, IllegalAccessException
  {
    // return fileType.getDataFileWriter(sourceDataFile);
    return new JsonFileWriter(sourceDataFile);
  }

  public DataFileFactory(File sourceDataFile)
  {
    this.sourceDataFile = sourceDataFile;
    fileType            = findByExtension(getExtension(sourceDataFile.getAbsolutePath()));
  }

  public String getDotPath()
  {
    return fileType.getDotPath(sourceDataFile.getAbsolutePath());
  }
}
