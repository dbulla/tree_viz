package com.nurflugel.dependencyvisualizer;

import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static com.nurflugel.dependencyvisualizer.DependencyObject.replaceAllBadChars;
import static com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.Down;
import static com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.Up;
import static org.testng.AssertJUnit.assertEquals;

/**  */
@SuppressWarnings({ "ProhibitedExceptionDeclared" })
public class ReaderWriterTest
{
  public static final Logger logger = LoggerFactory.getLogger(ReaderWriterTest.class);
  public static final String ITEM_D = "Item d";
  // -------------------------- OTHER METHODS --------------------------

  /**  */
  protected void doTestComparisons(File sourceDataFile, File expectedDotFile) throws IOException
  {
    File     resultFile     = new File(StringUtils.replace(sourceDataFile.getAbsolutePath(), ".txt", ".dot"));
    String[] testOutput     = getOutput(resultFile);
    String[] expectedOutput = getOutput(expectedDotFile);

    if (logger.isDebugEnabled())
    {
      logger.debug("Comparing " + expectedDotFile + " and " + resultFile);
    }

    assertEquals("Should have the same number of lines", expectedOutput.length, testOutput.length);

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
  String[] getOutput(File dotFile) throws IOException
  {
    List<String> strings = FileUtils.readLines(dotFile);

    return strings.toArray(new String[strings.size()]);
  }

  @Test
  public void testAutosys() throws IOException
  {
    File        sourceDataFile  = new File("build/resources/test/data/Autosys dependencies.txt");
    File        expectedDotFile = new File("build/resources/test/data/Autosys dependencies.dot_saved");
    DataHandler dataHandler     = new DataHandler(sourceDataFile);

    dataHandler.loadDataset();
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testNoFilters() throws IOException
  {
    File                    sourceDataFile     = new File("build/resources/test/data/test dependencies.txt");
    File                    expectedDotFile    = new File("build/resources/test/data/test dependencies.dot_saved");
    List<DirectionalFilter> directionalFilters = new ArrayList<>();
    List<DependencyObject>  keyObjects         = new ArrayList<>();
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
    File                    sourceDataFile     = new File("build/resources/test/data/test dependencies.txt");
    File                    expectedDotFile    = new File("build/resources/test/data/test filters up.dot");
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);
    List<DirectionalFilter> directionalFilters = new ArrayList<>();
    List<DependencyObject>  keyObjects         = new ArrayList<>();

    directionalFilters.add(Up);
    dataHandler.loadDataset();

    DependencyObject object = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D));

    keyObjects.add(object);
    dataHandler.setKeyObjectsToFilterOn(keyObjects);
    dataHandler.setDirectionalFilters(directionalFilters);
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testUpFiltersOnCdm() throws IOException
  {
    File                    sourceDataFile     = new File("build/resources/test/data/Autosys dependencies.txt");
    File                    expectedDotFile    = new File("build/resources/test/data/test filters up cdm.dot");
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);
    List<DirectionalFilter> directionalFilters = new ArrayList<>();
    List<DependencyObject>  keyObjects         = new ArrayList<>();

    directionalFilters.add(Up);
    dataHandler.loadDataset();

    DependencyObject object = new DependencyObject("cdm_style", Ranking.first().getName());

    keyObjects.add(object);
    dataHandler.setKeyObjectsToFilterOn(keyObjects);
    dataHandler.setDirectionalFilters(directionalFilters);
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testDownFilters() throws Exception
  {
    File                    sourceDataFile     = new File("build/resources/test/data/test dependencies.txt");
    File                    expectedDotFile    = new File("build/resources/test/data/test filters down.dot");
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);
    List<DirectionalFilter> directionalFilters = new ArrayList<>();
    List<DependencyObject>  keyObjects         = new ArrayList<>();

    directionalFilters.add(Down);
    dataHandler.loadDataset();

    DependencyObject object = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D));

    keyObjects.add(object);
    dataHandler.setKeyObjectsToFilterOn(keyObjects);
    dataHandler.setDirectionalFilters(directionalFilters);
    dataHandler.doIt();
    doTestComparisons(sourceDataFile, expectedDotFile);
  }

  @Test
  public void testUpAndDownFilters() throws Exception
  {
    File                    sourceDataFile     = new File("build/resources/test/data/test dependencies.txt");
    File                    expectedDotFile    = new File("build/resources/test/data/test filters up and down.dot");
    DataHandler             dataHandler        = new DataHandler(sourceDataFile);
    List<DirectionalFilter> directionalFilters = new ArrayList<>();
    List<DependencyObject>  keyObjects         = new ArrayList<>();

    directionalFilters.add(Down);
    directionalFilters.add(Up);
    dataHandler.loadDataset();

    DependencyObject object = dataHandler.findObjectByName(replaceAllBadChars(ITEM_D));

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
