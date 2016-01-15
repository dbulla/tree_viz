package com.nurflugel.dependencyvisualizer.readers;

import com.nurflugel.dependencyvisualizer.DependencyDataSet;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.File;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:45:13 PM To change this template use File | Settings | File Templates. */
@Data
@NoArgsConstructor
public class XmlFileReader extends DataFileReader
{
  public XmlFileReader(File sourceDataFile)
  {
    super(sourceDataFile);
  }

  public DependencyDataSet readObjectsFromFile()
  {
    return null;
  }

  public boolean isFamilyTree()
  {
    return false;
  }

  @Override
  protected DependencyDataSet parseLines()
  {
    return null;
  }
}
