package com.nurflugel.dependencyvisualizer.writers;

import com.nurflugel.dependencyvisualizer.DependencyObject;
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import com.nurflugel.dependencyvisualizer.enums.RankingName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import static java.util.stream.Collectors.toCollection;

/**  */
public class DotFileWriter
{
  public static final Logger logger     = LoggerFactory.getLogger(DotFileWriter.class);
  private File               dotFile;
  private boolean            doRankings;

  // --------------------------- CONSTRUCTORS ---------------------------
  public DotFileWriter(File dotFile, boolean doRankings)
  {
    this.dotFile    = dotFile;
    this.doRankings = doRankings;
  }

  /** Now, write the filtered objects back out to the file. */
  public void writeObjectsToDotFile(Collection<DependencyObject> objects, List<DirectionalFilter> directionalFilters) throws IOException
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("Writing output to file " + dotFile.getAbsolutePath());
    }

    try(OutputStream outputStream = new FileOutputStream(dotFile);
          DataOutputStream out = new DataOutputStream(outputStream))
    {
      Set<Ranking> types = getOnlyUsedTypes(objects);

      writeHeader(out);
      writeRankingEnumeration(out, types);
      writeObjectDeclarations(objects, out);
      writeRankingGroupings(objects, out, types);
      writeObjectDependencies(objects, out, directionalFilters);
      writeFooter(out);
    }
    catch (IOException e)
    {
      throw e;
    }
  }

  private Set<Ranking> getOnlyUsedTypes(Collection<DependencyObject> objects)
  {
    Set<Ranking> types = objects.stream()
                                .map(DependencyObject::getRanking)
                                .map(Ranking::valueOf)
                                .collect(toCollection(TreeSet::new));

    return types;
  }

  private void writeHeader(DataOutputStream out) throws IOException
  {
    String header = "digraph G {\n" + "node [shape=box,fontname=\"Arial\",fontsize=\"10\"];\n" + "edge [fontname=\"Arial\",fontsize=\"8\"];\n"
                      + "ranksep=.75;\n" + "rankdir=BT;\n" + "concentrate=true;\n\n";

    out.writeBytes(header);
  }

  /**
   * Write out ranking from the type enumerations. If I were to make this more general, it'd read these in from the file itelf, instead of a hardcoded
   * enum. The output looks like this:{
   *
   * <p>"CDM tables" -> "CDM loaders" -> "CDM views" -> "CDB views" -> "CDB tables"; }</p>
   */
  private Set<Ranking> writeRankingEnumeration(DataOutputStream out, Set<Ranking> types) throws IOException
  {
    if (doRankings)
    {
      out.writeBytes("node [shape=plaintext,fontname=\"Arial\",fontsize=\"10\"];\n");
      out.writeBytes("{ ");

      int i = 0;

      for (Ranking type : types)
      {
        if (i > 0)
        {
          out.writeBytes(" -> ");
        }

        out.writeBytes("\"" + type + '\"');
        i++;
      }

      out.writeBytes(" }\n\n");
    }

    return types;
  }

  private void writeObjectDeclarations(Collection<DependencyObject> objects, DataOutputStream out) throws IOException
  {
    for (DependencyObject object : objects)
    {
      RankingName   name        = object.getRanking();
      Ranking       type        = Ranking.valueOf(name);
      StringBuilder text        = new StringBuilder();
      String[]      notes       = object.getNotes();
      String        displayName;

      if (notes.length == 0)
      {
        displayName = object.getDisplayName();
      }
      else
      {
        StringBuilder displayText = new StringBuilder(object.getDisplayName());

        for (String note : notes)
        {
          displayText.append("\\n").append(note);
        }

        displayName = displayText.toString();
      }

      text.append(object.getName()).append(" [label=\"").append(displayName).append('\"');
      text.append(" shape=").append(type.getShape());
      text.append(" color=\"").append(type.getColor()).append("\"];\n");

      String outputText = text.toString();

      out.writeBytes(outputText);
    }

    out.writeBytes("\n\n");
  }

  /**
   * Write the actual groupings which tie the enum rankings with the objects. this ends up being a series of lines, like so:{ rank = same; "CDM
   * loaders"; "UpdateProd"; ..... }
   */
  private void writeRankingGroupings(Collection<DependencyObject> objects, DataOutputStream out, Set<Ranking> types) throws IOException
  {
    if (doRankings)
    {
      for (Ranking type : types)
      {
        out.writeBytes("{ rank = same; \"" + type + "\"; ");

        for (DependencyObject object : objects)
        {
          if (object.getRanking().equals(type))
          {
            out.writeBytes('\"' + object.getName() + "\"; ");
          }
        }

        out.writeBytes("}\n");
      }

      out.writeBytes("\n");
    }
  }

  private void writeObjectDependencies(Collection<DependencyObject> objects, DataOutputStream out, List<DirectionalFilter> directionalFilters)
                                throws IOException
  {
    // for (DependencyObject object : objects)
    // {
    // Collection<String> dependencies = object.getDependencies();
    //
    // for (String dependency : dependencies)
    // {
    // // if ((directionalFilters.isEmpty() || directionalFilters.contains(DirectionalFilter.Up)))
    // // {
    // // out.writeBytes(object.getName() + " -> " + dependency.getName() + ";\n");
    // // }
    // // else
    // if (objects.contains(dependency))
    // {
    // out.writeBytes(object.getName() + " -> " + dependency + ";\n");
    // }
    // }
    // }
    for (DependencyObject object : objects)
    {
      object.getDependencies()
            .stream()

            // .filter(objects::contains)
            .forEach(dependency ->
                     {
                       try
                       {
                         out.writeBytes(object.getName() + " -> " + dependency + ";\n");
                       }
                       catch (IOException e)
                       {
                         e.printStackTrace();
                       }
                       });
    }

    out.writeBytes("\n\n");
  }

  private void writeFooter(DataOutputStream out) throws IOException
  {
    String footer = "}\n";

    out.writeBytes(footer);
  }
}
