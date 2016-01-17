package com.nurflugel.dependencyvisualizer.readers;

import com.nurflugel.dependencyvisualizer.DependencyDataSet;
import com.nurflugel.dependencyvisualizer.DependencyObject;
import com.nurflugel.dependencyvisualizer.Person;
import com.nurflugel.dependencyvisualizer.enums.FileType;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import static com.nurflugel.dependencyvisualizer.DependencyObject.replaceAllBadChars;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:24:16 PM To change this template use File | Settings | File Templates. */
@Data
@NoArgsConstructor
public abstract class DataFileReader
{
  protected File     sourceDataFile;
  protected FileType fileType;
  private Logger     logger = LoggerFactory.getLogger(DataFileReader.class);

  protected DataFileReader(File sourceDataFile)
  {
    this.sourceDataFile = sourceDataFile;
  }

  /** Parse the individual line. */
  protected void parseObjectDeclaration(String line, Ranking ranking, DependencyDataSet dataSet)
  {
    String           lineText = line.trim();
    String[]         strings  = lineText.split("\\|");
    DependencyObject object;

    object = dataSet.isFamilyTree() ? new Person(strings[0].trim(), ranking.getName())
                                    : new DependencyObject(replaceAllBadChars(strings[0]), ranking.getName());

    if (strings.length > 1)
    {
      object.setDisplayName(strings[1].trim());
    }

    if (strings.length == 3)
    {
      String[] notes = strings[2].split("`");

      object.setNotes(notes);
    }

    dataSet.add(object);
  }

  // -------------------------- OTHER METHODS --------------------------
  public DependencyDataSet readObjectsFromFile()
  {
    DependencyDataSet dataSet = parseLines();

    return dataSet;
  }

  protected abstract DependencyDataSet parseLines();
}
