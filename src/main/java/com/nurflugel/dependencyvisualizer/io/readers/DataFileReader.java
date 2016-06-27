package com.nurflugel.dependencyvisualizer.io.readers;

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject;
import com.nurflugel.dependencyvisualizer.data.pojos.Person;
import com.nurflugel.dependencyvisualizer.enums.FileType;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:24:16 PM To change this template use File | Settings | File Templates. */
@Data
@NoArgsConstructor
public abstract class DataFileReader {
  protected File     sourceDataFile;
  protected FileType fileType;
  private Logger     logger = LoggerFactory.getLogger(DataFileReader.class);

  protected DataFileReader(File sourceDataFile) { this.sourceDataFile = sourceDataFile; }

  /** Parse the individual line. */
  protected void parseObjectDeclaration(String line, Ranking ranking, BaseDependencyDataSet dataSet) {
    String               lineText = line.trim();
    String[]             strings  = lineText.split("\\|");
    BaseDependencyObject object;
    String               name     = strings[0];

    object = dataSet.isFamilyTree() ? new Person(name, ranking.getName())
                                    : new DependencyObject(name, ranking.getName());

    if (strings.length > 1) { object.setDisplayName(strings[1].trim()); }

    if (strings.length == 3) {
      String[] notes = strings[2].split("`");

      object.setNotes(notes);
    }

    dataSet.add(object);
  }

  // -------------------------- OTHER METHODS --------------------------
  public BaseDependencyDataSet readObjectsFromFile() {
    BaseDependencyDataSet dataSet = parseLines();

    dataSet.generateRankingsMap();

    return dataSet;
  }

  protected abstract BaseDependencyDataSet parseLines();
}
