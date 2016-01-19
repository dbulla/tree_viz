package com.nurflugel.dependencyvisualizer.io.readers;

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.dataset.DependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet;
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.enums.Color;
import com.nurflugel.dependencyvisualizer.enums.FileType;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import com.nurflugel.dependencyvisualizer.enums.Shape;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.List;
import static com.nurflugel.dependencyvisualizer.Constants.*;

/**  */
@Data
@NoArgsConstructor
public class TextFileReader extends DataFileReader
{
  private static final Logger logger = LoggerFactory.getLogger(TextFileReader.class);
  // private boolean isFamilyTree;

  TextFileReader(File sourceDataFile)
  {
    super(sourceDataFile);
    fileType = FileType.TXT;
  }

  @Override
  @SuppressWarnings({ "ConstantConditions" })
  protected BaseDependencyDataSet parseLines()
  {
    // DependencyDataSet dataSet = new DependencyDataSet(new HashMap<>(), new ArrayList<>());
    List<String>          lines;
    boolean               isFamilyTree;
    BaseDependencyDataSet dataSet = new DependencyDataSet();

    try
    {
      lines = FileUtils.readLines(sourceDataFile);

      // else if (line.startsWith("&"))
      // {
      // isFamilyTree = parseFamilyHistory(line);
      // dataSet.setFamilyTree(isFamilyTree);
      // }
      isFamilyTree = lines.stream()
                          .filter(l -> l.startsWith("&"))
                          .filter(this::parseFamilyHistory)
                          .findFirst().isPresent();
      dataSet = isFamilyTree ? new FamilyTreeDataSet()
                             : new DependencyDataSet();

      Ranking currentRanking = null;
      boolean isDependencies = false;

      for (String line : lines)
      {
        if (logger.isDebugEnabled())
        {
          logger.debug(line);
        }

        if (!line.startsWith("//") && (!StringUtils.isEmpty(line)))
        {
          if (line.startsWith("#dependencies"))
          {
            isDependencies = true;
          }
          else if (line.startsWith("#"))
          {
            currentRanking = getObjectType(line);
          }
          else if (line.startsWith("&"))
          {
            isFamilyTree = parseFamilyHistory(line);
            dataSet.setFamilyTree(isFamilyTree);
          }
          else if (isDependencies)
          {
            try
            {
              parseDependency(line, dataSet);
            }
            catch (Exception e)
            {
              e.printStackTrace();
            }
          }
          else
          {
            parseObjectDeclaration(line, currentRanking, dataSet);
          }
        }
      }
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }

    return dataSet;
  }

  private boolean parseFamilyHistory(String line)
  {
    String[] nibbles = line.trim().split("=");
    boolean  b       = Boolean.parseBoolean(nibbles[1]);

    return b;
  }

  /**  */
  private Ranking getObjectType(String line)
  {
    String   strippedLine = line.substring(line.indexOf("#") + 1).trim();
    String[] chunks       = strippedLine.split(",");
    Shape    shape        = null;
    Color    color        = null;
    String   name         = null;

    for (String chunk : chunks)
    {
      String[] nibbles = chunk.trim().split("=");

      if (nibbles[0].trim().equalsIgnoreCase(NAME))
      {
        name = nibbles[1].trim();
      }
      else if (nibbles[0].trim().equalsIgnoreCase(COLOR))
      {
        color = Color.valueOf(nibbles[1].trim());
      }
      else if (nibbles[0].trim().equalsIgnoreCase(SHAPE))
      {
        shape = Shape.valueOf(nibbles[1].trim());
      }
    }

    return Ranking.valueOf(name, color, shape);
  }

  private void parseDependency(String line, BaseDependencyDataSet dataSet) throws Exception
  {
    try
    {
      String   lineToParse = line.trim();
      String[] chunks      = lineToParse.substring(0, lineToParse.length()).split("->");

      for (int i = 0; i < (chunks.length - 1); i++)
      {
        BaseDependencyObject main       = dataSet.getLoaderObjectByName(chunks[i]);
        BaseDependencyObject dependency = dataSet.getLoaderObjectByName(chunks[i + 1]);

        main.addDependency(dependency.getName());
      }
    }
    catch (Exception e)
    {
      logger.error("Error parsing dependency line: " + line, e);
      throw e;
    }
  }
}
