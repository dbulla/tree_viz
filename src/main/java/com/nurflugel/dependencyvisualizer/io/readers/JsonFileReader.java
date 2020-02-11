package com.nurflugel.dependencyvisualizer.io.readers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class JsonFileReader extends DataFileReader {
  static final Logger LOGGER = LoggerFactory.getLogger(JsonFileReader.class);

  JsonFileReader(File sourceDataFile) { super(sourceDataFile); }

  @Override
  @SuppressWarnings({ "ConstantConditions" })
  protected BaseDependencyDataSet parseLines() {
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson        gson        = gsonBuilder.create();

    // determine if it's a family tree BEFORE determining which type of data set
    List<String> lines;

    try {
      lines = FileUtils.readLines(sourceDataFile);
    }
    catch (IOException e) {
      e.printStackTrace();
      lines = new ArrayList<>();
    }

    boolean isFamilyTree = lines.stream()
                                .filter(l -> l.contains("isFamilyTree"))
                                .filter(l -> l.contains("true"))
                                .findFirst()
                                .isPresent();

    try(BufferedReader br = new BufferedReader(new FileReader(sourceDataFile))) {
      // convert the json string back to object
      BaseDependencyDataSet dataSet;
      Type                  theClazz = isFamilyTree ? FamilyTreeDataSet.class
                                                    : DependencyDataSet.class;

      dataSet = gson.fromJson(br, theClazz);
      dataSet.rectify();
      System.out.println("dataSet = " + dataSet);

      return dataSet;
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
