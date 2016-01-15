package com.nurflugel.dependencyvisualizer.readers;

import com.google.gson.Gson;
import com.nurflugel.dependencyvisualizer.DependencyDataSet;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Data
@NoArgsConstructor
public class JsonFileReader extends DataFileReader
{
  static final Logger logger = LoggerFactory.getLogger(JsonFileReader.class);

  JsonFileReader(File sourceDataFile)
  {
    super(sourceDataFile);
  }

  @SuppressWarnings({ "ConstantConditions" })
  protected DependencyDataSet parseLines()
  {
    Gson gson = new Gson();

    try(BufferedReader br = new BufferedReader(new FileReader(sourceDataFile)))
    {
      // convert the json string back to object
      DependencyDataSet dataSet = gson.fromJson(br, DependencyDataSet.class);

      dataSet.rectify();
      System.out.println("dataSet = " + dataSet);

      return dataSet;
    }
    catch (IOException e)
    {
      throw new RuntimeException(e);
    }
  }
}
