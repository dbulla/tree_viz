package com.nurflugel.dependencyvisualizer.writers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nurflugel.dependencyvisualizer.DataHandler;
import com.nurflugel.dependencyvisualizer.DependencyDataSet;
import com.nurflugel.dependencyvisualizer.DependencyObject;
import com.nurflugel.dependencyvisualizer.enums.FileType;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:46:12 PM To change this template use File | Settings | File Templates. */
public class JsonFileWriter extends DataFileWriter
{
  public JsonFileWriter(File sourceDataFile)
  {
    super(sourceDataFile);
  }

  @Override
  public void saveToFile(DataHandler dataHandler)
  {
    Set<DependencyObject> objects  = dataHandler.getObjects();
    List<Ranking>         rankings = objects.stream()
                                            .map(DependencyObject::getRanking)
                                            .distinct()
                                            .collect(toList());
    DependencyDataSet dataSet  = new DependencyDataSet(dataHandler.isFamilyTree(), objects, rankings);
    String            fileName = sourceDataFile.getAbsolutePath();
    String            baseName = FilenameUtils.getBaseName(fileName);

    fileName = baseName + FileType.JSON.getExtension();

    Gson gson = new GsonBuilder().setPrettyPrinting().create();

    // convert java object to JSON format,
    // and returned as JSON formatted string
    String json = gson.toJson(dataSet);

    try(FileWriter writer = new FileWriter(new File(sourceDataFile.getParent(), fileName)))
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
