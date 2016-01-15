package com.nurflugel.dependencyvisualizer.writers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nurflugel.dependencyvisualizer.DataHandler;
import com.nurflugel.dependencyvisualizer.DependencyDataSet;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import static com.nurflugel.dependencyvisualizer.enums.FileType.JSON;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:46:12 PM To change this template use File | Settings | File Templates. */
public class JsonFileWriter extends DataFileWriter
{
  public JsonFileWriter(File sourceDataFile)
  {
    super(sourceDataFile);
    fileType = JSON;
  }

  @Override
  public void saveToFile(DataHandler dataHandler)
  {
    DependencyDataSet dataSet  = dataHandler.getDataset();
    String            fileName = sourceDataFile.getAbsolutePath();
    String            baseName = FilenameUtils.getBaseName(fileName);

    fileName = baseName + fileType.getExtension();

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // convert java object to JSON format,
    // and returned as JSON formatted string
    String json = gson.toJson(dataSet);
    File   file = new File(sourceDataFile.getParent(), fileName);

    try(FileWriter writer = new FileWriter(file))
    {
      // write converted json data to a file named "file.json"
      writer.write(json);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }
}
