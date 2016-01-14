package com.nurflugel.dependencyvisualizer.readers;

import com.nurflugel.dependencyvisualizer.DependencyObject;
import com.nurflugel.dependencyvisualizer.Person;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import static java.util.stream.Collectors.toList;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:24:16 PM To change this template use File | Settings | File Templates. */
@Data
@NoArgsConstructor
public abstract class DataFileReader
{
  protected File sourceDataFile;
  private Logger logger = LoggerFactory.getLogger(DataFileReader.class);

  protected DataFileReader(File sourceDataFile)
  {
    this.sourceDataFile = sourceDataFile;
  }

  protected DependencyObject getLoaderObjectByName(String name, Set<DependencyObject> objects) throws Exception
  {
    String                     trimmedName = DependencyObject.cleanDefinition(name.trim());
    Optional<DependencyObject> optional    = objects.stream()
                                                    .filter(o -> o.getName().equals(trimmedName))
                                                    .findFirst();

    return optional.orElseGet(() ->
                              {
                                DependencyObject newObject = new DependencyObject(trimmedName, Ranking.first());

                                if (logger.isDebugEnabled())
                                {
                                  logger.debug("Adding unregistered object: " + trimmedName + " as object of type " + newObject.getRanking());
                                }

                                objects.add(newObject);

                                return newObject;
                              });
  }

  /** Parse the individual line. */
  protected void parseObjectDeclaration(String line, Ranking ranking, Set<DependencyObject> objects, boolean isFamilyHistory)
  {
    String           lineText = line.trim();
    String[]         strings  = lineText.split("\\|");
    DependencyObject object;

    object = isFamilyHistory ? new Person(strings[0].trim(), ranking)
                             : new DependencyObject(strings[0].trim(), ranking);

    if (strings.length > 1)
    {
      object.setDisplayName(strings[1].trim());
    }

    if (strings.length == 3)
    {
      String[] notes = strings[2].split("`");

      object.setNotes(notes);
    }

    objects.add(object);
  }

  // -------------------------- OTHER METHODS --------------------------
  public Set<DependencyObject> readObjectsFromFile()
  {
    Set<DependencyObject> objects = new TreeSet<>();

    try(InputStream fileInputStream = new FileInputStream(sourceDataFile);
          InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
          BufferedReader reader = new BufferedReader(inputStreamReader))
    {
      List<String> lines = reader.lines()
                                 .collect(toList());

      parseLines(lines, objects);
    }
    catch (Exception e)
    {
      logger.error("Error reading file", e);
    }

    return objects;
  }

  public abstract boolean isFamilyTree();

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
  protected abstract void parseLines(List<String> lines, Set<DependencyObject> objects) throws Exception;
}
