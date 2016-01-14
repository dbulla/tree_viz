package com.nurflugel.dependencyvisualizer.readers;

import com.google.gson.Gson;
import com.nurflugel.dependencyvisualizer.DependencyDataSet;
import com.nurflugel.dependencyvisualizer.DependencyObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
public class JsonFileReader extends DataFileReader
{
  static final Logger logger     = LoggerFactory.getLogger(JsonFileReader.class);
  private boolean     familyTree;

  JsonFileReader(File sourceDataFile)
  {
    super(sourceDataFile);
  }

  /** is this a family tree? */
  @Override
  public boolean isFamilyTree()
  {
    return familyTree;
  }

  /**
   * Read in each line and do one of several things:<br>
   * 1. Store the object declaration<br>
   * 1a. use the "#" comment to identify grouping (CDB tables, views, CDM, etc). Store these in a list for future filtering<br>
   * 2. Store the object dependency pair<br>
   * 3. Ignore other GraphViz comments
   *
   * @param  lines  - raw strings from file
   */
  @SuppressWarnings({ "ConstantConditions" })
  protected void parseLines(List<String> lines, Set<DependencyObject> objects) throws Exception
  {
    Gson           gson = new Gson();
    BufferedReader br   = new BufferedReader(new FileReader(sourceDataFile));

    // convert the json string back to object
    DependencyDataSet dataSet = gson.fromJson(br, DependencyDataSet.class);

    objects.addAll(dataSet.getObjects());
    System.out.println("dataSet = " + dataSet);
  }

  // /**  */
  // private Ranking getObjectType(String line)
  // {
  // String   strippedLine = line.substring(line.indexOf("#") + 1).trim();
  // String[] chunks       = strippedLine.split(",");
  // Shape    shape        = null;
  // Color    color        = null;
  // String   name         = null;
  //
  // for (String chunk : chunks)
  // {
  // String[] nibbles = chunk.trim().split("=");
  //
  // if (nibbles[0].trim().equalsIgnoreCase(NAME))
  // {
  // name = nibbles[1].trim();
  // }
  // else if (nibbles[0].trim().equalsIgnoreCase(COLOR))
  // {
  // color = Color.valueOf(nibbles[1].trim());
  // }
  // else if (nibbles[0].trim().equalsIgnoreCase(SHAPE))
  // {
  // shape = Shape.valueOf(nibbles[1].trim());
  // }
  // }
  //
  // return Ranking.valueOf(name, color, shape);
  // }
  private void parseDependency(String line, Set<DependencyObject> objects) throws Exception
  {
    try
    {
      String   lineToParse = line.trim();
      String[] chunks      = lineToParse.substring(0, lineToParse.length()).split("->");

      for (int i = 0; i < (chunks.length - 1); i++)
      {
        DependencyObject main       = getLoaderObjectByName(chunks[i], objects);
        DependencyObject dependency = getLoaderObjectByName(chunks[i + 1], objects);

        main.addDependency(dependency);
      }
    }
    catch (Exception e)
    {
      logger.error("Error parsing dependency line: " + line, e);
      throw e;
    }
  }
}
