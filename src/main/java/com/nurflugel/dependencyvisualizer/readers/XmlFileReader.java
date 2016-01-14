package com.nurflugel.dependencyvisualizer.readers;

import com.nurflugel.dependencyvisualizer.DependencyObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.File;
import java.util.List;
import java.util.Set;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:45:13 PM To change this template use File | Settings | File Templates. */
@Data
@NoArgsConstructor
public class XmlFileReader extends DataFileReader
{
  public XmlFileReader(File sourceDataFile)
  {
    super(sourceDataFile);
  }

  public Set<DependencyObject> readObjectsFromFile()
  {
    return null;
  }

  public boolean isFamilyTree()
  {
    return false;
  }

  @Override
  protected void parseLines(List<String> lines, Set<DependencyObject> objects) throws Exception {}
}
