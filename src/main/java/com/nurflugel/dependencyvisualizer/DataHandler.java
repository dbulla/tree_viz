package com.nurflugel.dependencyvisualizer;

import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import com.nurflugel.dependencyvisualizer.readers.DataFileReader;
import com.nurflugel.dependencyvisualizer.writers.DataFileWriter;
import com.nurflugel.dependencyvisualizer.writers.DotFileWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**  */
@SuppressWarnings({ "ProhibitedExceptionThrown", "ProhibitedExceptionDeclared" })
public class DataHandler
{
  private static final Logger     logger             = LoggerFactory.getLogger(DataHandler.class);
  private File                    dotFile;
  private List<DirectionalFilter> directionalFilters = new ArrayList<DirectionalFilter>();
  private List<Ranking>           typesFilters       = new ArrayList<Ranking>();
  private Set<DependencyObject>   keyObjects         = new TreeSet<DependencyObject>();
  private Set<DependencyObject>   objects            = new TreeSet<DependencyObject>();
  private boolean                 isRanking          = true;
  private DataFileReader          dataFileReader;
  private DataFileWriter          dataFileWriter;
  private boolean                 isFamilyTree;

  public DataHandler(File sourceDataFile)
  {
    DataFileFactory dataFileFactory = new DataFileFactory(sourceDataFile);
    String          dotPath         = dataFileFactory.getDotPath();

    try
    {
      dataFileReader = dataFileFactory.getReader();
      dataFileWriter = dataFileFactory.getWriter();
    }
    catch (InstantiationException | IllegalAccessException e)
    {
      e.printStackTrace();
    }

    dotFile = new File(dotPath);
  }

  // -------------------------- OTHER METHODS --------------------------
  public File doIt()
  {
    Set<DependencyObject> filteredObjects = filterObjects();
    DotFileWriter         writer          = new DotFileWriter(dotFile, isRanking);

    try
    {
      writer.writeObjectsToDotFile(filteredObjects, directionalFilters);
    }
    catch (IOException e)
    {
      logger.error("Error writing file", e);
    }

    return dotFile;
  }

  /** Filter the objects based on criteria from the UI- - Specific objects - filter up or down - show/don't show tiers (no NSC, etc). */
  private Set<DependencyObject> filterObjects()
  {
    ObjectFilterer filter = new ObjectFilterer(directionalFilters, typesFilters);

    return filter.filter(objects, keyObjects);
  }

  /** See if the given object is found. if not, throw an excpetion. */
  public DependencyObject findObjectByName(String name) throws Exception
  {
    for (DependencyObject dependencyObject : objects)
    {
      if (dependencyObject.getName().equals(DependencyObject.cleanDefinition(name)))
      {
        return dependencyObject;
      }
    }

    throw new Exception("Object not found by name: " + name);
  }

  public void loadDataset()
  {
    objects      = dataFileReader.readObjectsFromFile();
    isFamilyTree = dataFileReader.isFamilyTree();
  }

  public Set<DependencyObject> getObjects()
  {
    return objects;
  }

  public void initialize()
  {
    directionalFilters = new ArrayList<DirectionalFilter>();
    typesFilters       = new ArrayList<Ranking>();
    keyObjects         = new TreeSet<DependencyObject>();
  }

  public void setDirectionalFilters(List<DirectionalFilter> directionalFilters)
  {
    this.directionalFilters = new ArrayList<DirectionalFilter>(directionalFilters);
  }

  public void setIsRanking(boolean isRanking)
  {
    this.isRanking = isRanking;
  }

  public void setKeyObjectsToFilterOn(List<DependencyObject> keyObjects)
  {
    this.keyObjects = new TreeSet<DependencyObject>(keyObjects);
  }

  public void setTypesFilters(List<Ranking> typesFilters)
  {
    this.typesFilters = new ArrayList<Ranking>(typesFilters);
  }

  public void saveDataset()
  {
    dataFileWriter.saveToFile(this);
  }

  public boolean isFamilyTree()
  {
    return isFamilyTree;
  }

  public void setIsFamilyTree(boolean familyTree)
  {
    isFamilyTree = familyTree;
  }
}
