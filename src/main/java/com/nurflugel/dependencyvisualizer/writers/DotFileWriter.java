package com.nurflugel.dependencyvisualizer.writers;

import com.nurflugel.dependencyvisualizer.DependencyObject;
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

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
  public void writeObjectsToDotFile(Collection<DependencyObject> objects, List<DirectionalFilter> directionalFilters) throws Exception
  {
    if (logger.isDebugEnabled())
    {
      logger.debug("Writing output to file " + dotFile.getAbsolutePath());
    }

    try(OutputStream outputStream = new FileOutputStream(dotFile);
          DataOutputStream out = new DataOutputStream(outputStream))
    {
      List<Ranking> types = getOnlyUsedTypes(objects);

      writeHeader(out);
      writeRankingEnumeration(out, types);
      writeObjectDeclarations(objects, out);
      writeRankingGroupings(objects, out, types);
      writeObjectDependencies(objects, out, directionalFilters);
      writeFooter(out);
    }
    catch (Exception e)
    {
      throw e;
    }
  }

  private List<Ranking> getOnlyUsedTypes(Collection<DependencyObject> objects)
  {
    List<Ranking> types = objects.stream()
                                 .map(DependencyObject::getRanking)
                                 .distinct()
                                 .map(Ranking::valueOf)
                                 .sorted(comparing(Ranking::getRank).reversed())
                                 .collect(toList());

    return types;
  }

  private void writeHeader(DataOutputStream out)
  {
    String header = "digraph G {\n" + "node [shape=box,fontname=\"Arial\",fontsize=\"10\"];\n" + "edge [fontname=\"Arial\",fontsize=\"8\"];\n"
                      + "ranksep=.75;\n" + "rankdir=BT;\n" + "concentrate=true;\n\n";

    writeToOutput(out, header);
  }

  /**
   * Write out ranking from the type enumerations. If I were to make this more general, it'd read these in from the file itelf, instead of a hardcoded
   * enum. The output looks like this:{
   *
   * <p>"CDM tables" -> "CDM loaders" -> "CDM views" -> "CDB views" -> "CDB tables"; }</p>
   */
  private List<Ranking> writeRankingEnumeration(DataOutputStream out, List<Ranking> types)
  {
    if (doRankings)
    {
      writeToOutput(out, "node [shape=plaintext,fontname=\"Arial\",fontsize=\"10\"];\n");
      writeToOutput(out, "{ ");

      String line = types.stream()
                         .map(type -> "\"" + type + '\"')
                         .collect(joining(" -> "));

      writeToOutput(out, line);
      writeToOutput(out, " }\n\n");
    }

    return types;
  }

  private void writeObjectDeclarations(Collection<DependencyObject> objects, DataOutputStream out)
  {
    objects.stream()
           .sorted()
           .forEach(object ->
                    {
                      String        name        = object.getRanking();
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

                      writeToOutput(out, outputText);
                      });
    writeToOutput(out, "\n\n");
  }

  /**
   * Write the actual groupings which tie the enum rankings with the objects. this ends up being a series of lines, like so:{ rank = same; "CDM
   * loaders"; "UpdateProd"; ..... }
   */
  private void writeRankingGroupings(Collection<DependencyObject> objects, DataOutputStream out, List<Ranking> types)
  {
    if (doRankings)
    {
      for (Ranking type : types)
      {
        writeToOutput(out, "{ rank = same; \"" + type + "\"; ");
        objects.stream()
               .filter(object -> object.getRanking().equals(type.getName()))
               .sorted()
               .forEach(object -> writeToOutput(out, '\"' + object.getName() + "\"; "));
        writeToOutput(out, "}\n");
      }

      writeToOutput(out, "\n\n");
    }
  }

  private void writeObjectDependencies(Collection<DependencyObject> objects, DataOutputStream out, List<DirectionalFilter> directionalFilters)
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
    List<String> lines = new ArrayList<>();

    for (DependencyObject object : objects)
    {
      object.getDependencies()
            .stream()

            // .filter(objects::contains)
            .forEach(dependency -> lines.add(object.getName() + " -> " + dependency + ";\n"));
    }

    lines.stream()
         .sorted()
         .forEach(l -> writeToOutput(out, l));
    writeToOutput(out, "\n\n");
  }

  /** method to suppress checked exceptions). */
  private void writeToOutput(DataOutputStream out, String text)
  {
    try
    {
      out.writeBytes(text);
    }
    catch (IOException e)
    {
      throw new RuntimeException("Error writing to output", e);
    }
  }

  private void writeFooter(DataOutputStream out)
  {
    String footer = "}\n";

    writeToOutput(out, footer);
  }
}
