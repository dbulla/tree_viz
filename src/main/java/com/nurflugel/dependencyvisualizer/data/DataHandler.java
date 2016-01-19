package com.nurflugel.dependencyvisualizer.data;

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import com.nurflugel.dependencyvisualizer.io.DataFileFactory;
import com.nurflugel.dependencyvisualizer.io.readers.DataFileReader;
import com.nurflugel.dependencyvisualizer.io.writers.DataFileWriter;
import com.nurflugel.dependencyvisualizer.io.writers.DotFileWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.*;

/**  */
@SuppressWarnings({ "ProhibitedExceptionThrown", "ProhibitedExceptionDeclared" })
public class DataHandler
{
  private static final Logger       logger             = LoggerFactory.getLogger(DataHandler.class);
  private File                      dotFile;
  private List<DirectionalFilter>   directionalFilters = new ArrayList<>();
  private List<Ranking>             typesFilters       = new ArrayList<>();
  private Set<BaseDependencyObject> keyObjects         = new TreeSet<>();
  private boolean                   isRanking          = true;
  private DataFileReader            dataFileReader;
  private DataFileWriter            dataFileWriter;
  private BaseDependencyDataSet     dataset;

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
    Collection<BaseDependencyObject> filteredObjects = filterObjects();
    DotFileWriter                    writer          = new DotFileWriter(dotFile, isRanking);

    try
    {
      writer.writeObjectsToDotFile(filteredObjects);
    }
    catch (Exception e)
    {
      logger.error("Error writing file", e);
    }

    return dotFile;
  }

  /** Filter the objects based on criteria from the UI- - Specific objects - filter up or down - show/don't show tiers (no NSC, etc). */
  private Collection<BaseDependencyObject> filterObjects()
  {
    ObjectFilterer filter = new ObjectFilterer(directionalFilters, typesFilters);

    return filter.filter(dataset, keyObjects);
  }

  /** See if the given object is found. if not, throw an excpetion. */
  public BaseDependencyObject findObjectByName(String name) throws Exception
  {
    String               cleanName            = BaseDependencyObject.replaceAllBadChars(name);
    BaseDependencyObject baseDependencyObject = dataset.getObjects()
                                                       .filter(o -> StringUtils.equals(o.getName(), cleanName))
                                                       .findFirst()
                                                       .orElseThrow(() -> new Exception("Object not found by name: " + name));

    return baseDependencyObject;
  }

  public void loadDataset()
  {
    dataset = dataFileReader.readObjectsFromFile();
  }

  public void initialize()
  {
    directionalFilters = new ArrayList<>();
    typesFilters       = new ArrayList<>();
    keyObjects         = new TreeSet<>();
  }

  public void setDirectionalFilters(List<DirectionalFilter> directionalFilters)
  {
    this.directionalFilters = new ArrayList<>(directionalFilters);
  }

  public void setIsRanking(boolean isRanking)
  {
    this.isRanking = isRanking;
  }

  public void setKeyObjectsToFilterOn(List<BaseDependencyObject> keyObjects)
  {
    this.keyObjects = new TreeSet<>(keyObjects);
  }

  public void setTypesFilters(List<Ranking> typesFilters)
  {
    this.typesFilters = new ArrayList<>(typesFilters);
  }

  public void saveDataset()
  {
    dataFileWriter.saveToFile(this);
  }

  public BaseDependencyDataSet getDataset()
  {
    return dataset;
  }
}
