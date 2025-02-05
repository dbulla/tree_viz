package com.nurflugel.dependencyvisualizer.io.writers

import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet
import com.nurflugel.dependencyvisualizer.data.dataset.FamilyTreeDataSet
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.Person
import com.nurflugel.dependencyvisualizer.enums.Ranking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 *
 */
class DotFileWriter(private val dotFile: File, private val doRankings: Boolean) {

    /** Write the filtered objects back out to the file. */
    fun writeObjectsToDotFile(objects: Collection<BaseDependencyObject>, dataset: BaseDependencyDataSet) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Writing output to file " + dotFile.absolutePath)
        }

        try {
            FileOutputStream(dotFile).use { outputStream ->
                DataOutputStream(outputStream).use { out ->
                    val rankings = getOnlyUsedRankings(objects)
                    writeHeader(out)
                    writeRankingEnumeration(out, rankings)
                    writeObjectDeclarations(objects, out, dataset)
                    writeRankingGroupings(objects, out, rankings)
                    writeObjectDependencies(objects, out)
                    //                    writeSpouses(objects, out);
                    writeFooter(out)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun getOnlyUsedRankings(objects: Collection<BaseDependencyObject>): List<Ranking> {
        val types = objects
            .map(BaseDependencyObject::ranking)
            .distinct()
            .map { Ranking.valueOf(it) }
            .sortedDescending()
            .toList()

        return types
    }

    private fun writeHeader(out: DataOutputStream) {
        writeToComment(out, "Header")

        // + "ranksep=.75;\n"
        val header = ("""
     digraph G {
     node [shape=box,fontname="Arial",fontsize="10"];
     edge [fontname="Arial",fontsize="8"];
     ranksep=1.5;
     rankdir=BT;
     concentrate=true;
     
     
     """.trimIndent())

        writeToOutput(out, header)
    }

    /**
     * Write out ranking from the type enumerations. If I were to make this more general, it'd read these in from the file itself, instead of a hardcoded enum. The output looks like
     * this: { "CDM tables" -> "CDM loaders" -> "CDM views" -> "CDB views" -> "CDB tables"; }
     */
    private fun writeRankingEnumeration(out: DataOutputStream, types: List<Ranking>): List<Ranking> {
        if (doRankings) {
            writeToComment(out, "Ranking Enumeration")
            writeToOutput(out, "node [shape=plaintext,fontname=\"Arial\",fontsize=\"10\"];\n")
            writeToOutput(out, "{ ")

            val line = types.joinToString(" -> ") { "\"" + it.name + '\"' }

            writeToOutput(out, line)
            //            writeToOutput(out, line.joinToString {  " -> " })
            writeToOutput(out, " }\n\n")
        }

        return types
    }

    private fun writeObjectDeclarations(objects: Collection<BaseDependencyObject>, out: DataOutputStream, dataset: BaseDependencyDataSet) {
        writeToComment(out, "Declarations")
        objects
            .sorted()
            .forEach { dependencyObject: BaseDependencyObject ->
                val ranking = Ranking.valueOf(dependencyObject.ranking)
                val stringBuilder = StringBuilder()
                val notes = dependencyObject.notes
                val displayName: String

                val displayText = StringBuilder(dependencyObject.displayName)
                if (dependencyObject is Person) {
                    if (dependencyObject.birthDate != null) displayText.append("\\nb: ").append(dependencyObject.birthDate)
                    if (dependencyObject.deathDate != null) displayText.append("\\nd: ").append(dependencyObject.deathDate)
                    (dataset as FamilyTreeDataSet).getMarriagesForPerson(dependencyObject).forEach { marriage ->
                        val spouseName = marriage.getSpouse(dependencyObject.name)
                        val spouseDisplayName = dataset.objectByName(spouseName).displayName
                        displayText.append("\\nm: ").append(spouseDisplayName)
                        if (marriage.marriageDate != null) displayText.append(" ").append(marriage.marriageDate)
                    }
                }
                for (note in notes) {
                    displayText.append("\\n").append(note)
                }

                displayName = displayText.toString()

                stringBuilder.append(dependencyObject.name).append(" [label=\"").append(displayName).append('\"')
                stringBuilder.append(" shape=").append(ranking.shape)
                stringBuilder.append(" color=\"").append(ranking.color).append("\"];\n")

                val outputText = stringBuilder.toString()
                writeToOutput(out, outputText)
            }
        writeToOutput(out, "\n\n")
    }

    /**
     * Write the actual groupings which tie the enum rankings with the objects. this ends up being a series of lines, like so:{ rank = same; "CDM loaders"; "UpdateProd"; ..... }
     */
    private fun writeRankingGroupings(objects: Collection<BaseDependencyObject>, out: DataOutputStream, types: List<Ranking>) {
        if (doRankings) {
            writeToComment(out, "Ranking groupings")

            for (type in types) {
                writeToOutput(out, "{ rank = same; \"$type\"; ")
                objects
                    .filter { it.ranking == type.name }
                    .sorted()
                    .forEach { writeToOutput(out, '\"'.toString() + it.name + "\"; ") }
                writeToOutput(out, "}\n")
            }

            writeToOutput(out, "\n\n")
        }
    }

    private fun writeObjectDependencies(objects: Collection<BaseDependencyObject>, out: DataOutputStream) {
        writeToComment(out, "Dependencies")

        val names = objects.map { it.name }.toSet()
        val lines: MutableList<String> = mutableListOf()

        objects.forEach { dependencyObject ->
            dependencyObject.dependencies
                .filter { names.contains(it) }
                .forEach { lines.add("${dependencyObject.name} -> $it;\n") }
        }

        lines.sorted()
            .forEach { writeToOutput(out, it) }

        writeToOutput(out, "\n\n")
    }

    //    private fun writeSpouses(objects: Collection<BaseDependencyObject>, out: DataOutputStream) {
    //        val lines: MutableList<String> = mutableListOf()
    //        val names = objects
    //            .map(BaseDependencyObject::name)
    //
    //        objects
    //            .filterIsInstance<Person>()
    //            .forEach { it ->
    //                it.spouses
    //                    .filter { names.contains(it) }
    //                    .map { spouse -> it.name + " -> " + spouse + ";\n" }
    //                    .forEach(lines::add)
    //            }
    //
    //        if (lines.isNotEmpty()) {
    //            writeToComment(out, "Spouses")
    //            writeToOutput(out, "edge [color=red,arrowhead=none]\n")
    //        }
    //
    //        lines
    //            .sorted()
    //            .forEach { writeToOutput(out, it) }
    //        writeToOutput(out, "\n\n")
    //    }

    /** method to suppress checked exceptions. */
    private fun writeToOutput(out: DataOutputStream, text: String) {
        try {
            out.writeBytes(text)
        } catch (e: IOException) {
            throw RuntimeException("Error writing to output", e)
        }
    }

    private fun writeToComment(out: DataOutputStream, text: String) {
        writeToOutput(out, "//$text\n")
    }

    private fun writeFooter(out: DataOutputStream) {
        val footer = "}\n"

        writeToOutput(out, footer)
    }

    companion object {
        val LOGGER: Logger = LoggerFactory.getLogger(DotFileWriter::class.java)
    }
}
