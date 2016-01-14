package com.nurflugel.dependencyvisualizer.readers;

import com.nurflugel.dependencyvisualizer.DependencyObject;
import com.nurflugel.dependencyvisualizer.enums.Color;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import com.nurflugel.dependencyvisualizer.enums.Shape;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.List;
import java.util.Set;
import static com.nurflugel.dependencyvisualizer.Constants.*;

/**  */
@Data
@NoArgsConstructor
public class TextFileReader extends DataFileReader
{
  static final Logger logger     = LoggerFactory.getLogger(TextFileReader.class);
  private boolean     familyTree;

  TextFileReader(File sourceDataFile)
  {
    super(sourceDataFile);
  }

  /** is this a family tree? */
  @Override
  public boolean isFamilyTree()
  {
    return familyTree;
  }

  @Override
  @SuppressWarnings({ "ConstantConditions" })
  protected void parseLines(List<String> lines, Set<DependencyObject> objects) throws Exception
  {
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
          familyTree = parseFamilyHistory(line);
        }
        else if (isDependencies)
        {
          parseDependency(line, objects);
        }
        else
        {
          parseObjectDeclaration(line, currentRanking, objects, familyTree);
        }
      }
    }
  }

  private boolean parseFamilyHistory(String line)
  {
    String[] nibbles = line.trim().split("=");

    return Boolean.parseBoolean(nibbles[1]);
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
