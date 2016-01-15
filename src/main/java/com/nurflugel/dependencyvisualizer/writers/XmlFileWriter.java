package com.nurflugel.dependencyvisualizer.writers;

import com.nurflugel.dependencyvisualizer.DataHandler;
import com.nurflugel.dependencyvisualizer.DependencyObject;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import org.apache.commons.lang3.StringUtils;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import static com.nurflugel.dependencyvisualizer.Constants.*;

/** Created by IntelliJ IDEA. User: douglasbullard Date: Jan 4, 2008 Time: 5:46:12 PM To change this template use File | Settings | File Templates. */
public class XmlFileWriter extends DataFileWriter
{
  public XmlFileWriter(File sourceDataFile)
  {
    super(sourceDataFile);
  }

  @Override
  public void saveToFile(DataHandler dataHandler)
  {
    Element  root  = new Element(ROOT);
    Document doc   = new Document(root);
    String   value = dataHandler.getDataset().isFamilyTree() ? TRUE
                                                             : FALSE;

    root.addContent(new Element(IS_FAMILY_TREE).setAttribute(VALUE, value));
    root.addContent(Ranking.getElements());

    Element objectsElement = new Element(DEPENDENCY_OBJECTS);

    root.addContent(objectsElement);

    for (DependencyObject object : dataHandler.getObjects())
    {
      Element objectElement = object.getElement();

      objectsElement.addContent(objectElement);
    }

    // outputDependencies(root, objects);
    Format       prettyFormat = Format.getPrettyFormat();
    XMLOutputter outp         = new XMLOutputter(prettyFormat);
    String       fileName     = sourceDataFile.getAbsolutePath();

    fileName = StringUtils.replace(fileName, TXT, XML);

    try
    {
      OutputStream fileStream = new FileOutputStream(fileName);

      outp.output(doc, fileStream);
      // Compressed output
      // Format format=new Format("dibble");
      // outp.set  setTextTrim(true);
      // outp.output(doc, socketStream);
      //
      // outp.setTextTrim(true);
      // outp.setIndent(" ");
      // outp.setNewlines(true);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  // private void outputDependencies(Element                root,
  // List<DependencyObject> objects)
  // {
  // Element dependenciesElement = new Element("dependencies");
  // root.addContent(dependenciesElement);
  //
  // for (DependencyObject dependentObject : objects) {
  // Element dependencyElement = new Element("dependency");
  // dependenciesElement.addContent(dependencyElement);
  // dependencyElement.setAttribute("dependentObject", dependentObject.getName());
  //
  // List<DependencyObject> dependencies = dependentObject.getDependencies();
  //
  // for (DependencyObject dependency : dependencies) {
  // Element element = new Element("objectDependedOn");
  // dependenciesElement.addContent(element);
  // element.setAttribute("name", dependency.getName());
  // }
  //
  // dependencyElement.setAttribute("dependency", dependentObject.getName());
  // }
  //
  // }
}
