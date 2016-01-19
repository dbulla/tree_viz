package com.nurflugel.dependencyvisualizer.io.writers;

import com.google.gson.GsonBuilder;
import com.nurflugel.dependencyvisualizer.data.DataHandler;
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet;
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
    BaseDependencyDataSet dataSet = dataHandler.getDataset();

    dataSet.generateRankingsMap();

    String fileName  = sourceDataFile.getAbsolutePath();
    String baseName  = FilenameUtils.getBaseName(fileName);

    fileName = baseName + fileType.getExtension();

    // GsonBuilder gson = new GsonBuilder();
    // gson.registerTypeAdapter(A.class, new ATypeAdapter());
    // String json = gson.create().toJson(list);
    GsonBuilder gson = new GsonBuilder().setPrettyPrinting();

    // gson.registerTypeAdapter(DependencyObject.class, new DependencyObjectAdapter());
    // gson.registerTypeAdapter(Person.class, new PersonAdapter());
    String json = gson.create().toJson(dataSet);
    // Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // convert java object to JSON format,
    // and returned as JSON formatted string
    // String json = gson.toJson(dataSet);
    File file = new File(sourceDataFile.getParent(), fileName);

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
