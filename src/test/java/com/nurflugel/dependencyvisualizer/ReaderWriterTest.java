package com.nurflugel.dependencyvisualizer;

import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static org.testng.AssertJUnit.assertEquals;

/**  */
@SuppressWarnings({ "ProhibitedExceptionDeclared" })
public class ReaderWriterTest
{
  public static final Logger logger = LoggerFactory.getLogger(ReaderWriterTest.class);
  public static final String ITEM_D = "Item d";
  // -------------------------- OTHER METHODS --------------------------

  /**  */
  protected void doTestComparisons(File sourceDataFile, File expectedDotFile)
  {
    File     resultFile     = new File(StringUtils.replace(sourceDataFile.getAbsolutePath(), ".txt", ".dot"));
    String[] testOutput     = getOutput(resultFile);
    String[] expectedOutput = getOutput(expectedDotFile);

    if (logger.isDebugEnabled())
    {
      logger.debug("Comparing " + expectedDotFile + " and " + resultFile);
    }

    assertEquals("Should have the smae number of lines", expectedOutput.length, testOutput.length);

    for (int i = 0; i < expectedOutput.length; i++)
    {
      assertEquals("Test output should be equal to expected output", expectedOutput[i], testOutput[i]);
    }
  }

  /**
   * Gets the expected output from the .dot file.
   *
   * @param   dotFile  The .dot file to read
   *
   * @return  A string array, where each element represents one line of the.dot tex file.
   */
  String[] getOutput(File dotFile)
  {
    List<String>   strings = new ArrayList<String>();
    BufferedReader input   = null;

    try
    {
      input = new BufferedReader(new FileReader(dotFile));

      String line = input.readLine();

      while (line != null)
      {
        strings.add(line.trim());
        line = input.readLine();
      }
    }
    catch (IOException e)
    {
      logger.error("message", e);
    }
    finally
    {
      try
      {
        assert input != null;
        input.close();
      }
      catch (IOException e)
      {
        logger.error("Error", e);
      }
    }

    return strings.toArray(new String[strings.size()]);
  }

  @Test
  public void testAutosys()
  {
    File        sourceDataFile  = new File("Test data/Autosys dependencies.txt");
    File        expectedDotFile = new File("Test data/Autosys dependencies.dot_saved");
    DataHandler dataHandler     = new DataHandler(sourceDataFile);

    dataHandler.loadDataset();
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testNoFilters()
  {
    File                    sourceDataFile     = new File("Test data/test dependencies.txt");
    File                    expectedDotFile    = new File("Test data/test dependencies.dot_saved");
    List<DirectionalFilter> directionalFilters = new ArrayList<DirectionalFilter>();
    List<DependencyObject>  keyObjects         = new ArrayList<DependencyObject>();
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);

    dataHandler.setKeyObjectsToFilterOn(keyObjects);
    dataHandler.setDirectionalFilters(directionalFilters);
    dataHandler.loadDataset();
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testUpFilters() throws Exception
  {
    File                    sourceDataFile     = new File("Test data/test dependencies.txt");
    File                    expectedDotFile    = new File("Test data/test filters up.dot");
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);
    List<DirectionalFilter> directionalFilters = new ArrayList<DirectionalFilter>();
    List<DependencyObject>  keyObjects         = new ArrayList<DependencyObject>();

    directionalFilters.add(DirectionalFilter.Up);
    dataHandler.loadDataset();

    DependencyObject object = dataHandler.findObjectByName(DependencyObject.replaceAllBadChars(ITEM_D));

    keyObjects.add(object);
    dataHandler.setKeyObjectsToFilterOn(keyObjects);
    dataHandler.setDirectionalFilters(directionalFilters);
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testUpFiltersOnCdm()
  {
    File                    sourceDataFile     = new File("Test data/Autosys dependencies.txt");
    File                    expectedDotFile    = new File("Test data/test filters up cdm.dot");
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);
    List<DirectionalFilter> directionalFilters = new ArrayList<DirectionalFilter>();
    List<DependencyObject>  keyObjects         = new ArrayList<DependencyObject>();

    directionalFilters.add(DirectionalFilter.Up);
    dataHandler.loadDataset();

    DependencyObject object = new DependencyObject("cdm_style", Ranking.first());

    keyObjects.add(object);
    dataHandler.setKeyObjectsToFilterOn(keyObjects);
    dataHandler.setDirectionalFilters(directionalFilters);
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testDownFilters() throws Exception
  {
    File                    sourceDataFile     = new File("Test data/test dependencies.txt");
    File                    expectedDotFile    = new File("Test data/test filters down.dot");
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);
    List<DirectionalFilter> directionalFilters = new ArrayList<DirectionalFilter>();
    List<DependencyObject>  keyObjects         = new ArrayList<DependencyObject>();

    directionalFilters.add(DirectionalFilter.Down);
    dataHandler.loadDataset();

    DependencyObject object = dataHandler.findObjectByName(DependencyObject.replaceAllBadChars(ITEM_D));

    keyObjects.add(object);
    dataHandler.setKeyObjectsToFilterOn(keyObjects);
    dataHandler.setDirectionalFilters(directionalFilters);
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testUpAndDownFilters() throws Exception
  {
    File                    sourceDataFile     = new File("Test data/test dependencies.txt");
    File                    expectedDotFile    = new File("Test data/test filters up and down.dot");
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);
    List<DirectionalFilter> directionalFilters = new ArrayList<DirectionalFilter>();
    List<DependencyObject>  keyObjects         = new ArrayList<DependencyObject>();

    directionalFilters.add(DirectionalFilter.Down);
    directionalFilters.add(DirectionalFilter.Up);
    dataHandler.loadDataset();

    DependencyObject object = dataHandler.findObjectByName(DependencyObject.replaceAllBadChars(ITEM_D));

    keyObjects.add(object);
    dataHandler.setKeyObjectsToFilterOn(keyObjects);
    dataHandler.setDirectionalFilters(directionalFilters);
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  // public void testDownFilters()
  // {
  // File                    sourceDataFile     = new File("Test data/test dependencies.txt");
  // File                    expectedDotFile    = new File("Test data/test filters down.dot");
  // Handler            readerWriter       = new Handler(sourceDataFile);
  // List<DirectionalFilter> directionalFilters = new ArrayList<DirectionalFilter>();
  //
  // directionalFilters.valueOf(DirectionalFilter.Down);
  // readerWriter.setDirectionalFilters(directionalFilters);
  // readerWriter.doIt();
  // doTestComparisons(sourceDataFile, expectedDotFile);
  // }
}
