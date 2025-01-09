package com.nurflugel.dependencyvisualizer.io.writers

import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject
import com.nurflugel.dependencyvisualizer.data.pojos.Person
import com.nurflugel.dependencyvisualizer.enums.Ranking
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.stream.Collectors

/**
 *
 */
class DotFileWriter(private val dotFile: File, private val doRankings: Boolean) {
    /**
     * Now, write the filtered objects back out to the file.
     */
    @Throws(Exception::class)
    fun writeObjectsToDotFile(objects: Collection<BaseDependencyObject>) {
        if (LOGGER.isDebugEnabled) {
            LOGGER.debug("Writing output to file " + dotFile.absolutePath)
        }

        try {
            FileOutputStream(dotFile).use { outputStream ->
                DataOutputStream(outputStream).use { out ->
                    val types = getOnlyUsedTypes(objects)
                    writeHeader(out)
                    writeRankingEnumeration(out, types)
                    writeObjectDeclarations(objects, out)
                    writeRankingGroupings(objects, out, types)
                    writeObjectDependencies(objects, out)

                    // writeSpouses(objects, out);
                    writeFooter(out)
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }

    private fun getOnlyUsedTypes(objects: Collection<BaseDependencyObject>): List<Ranking> {
        val types = objects
            .asSequence()
            .map(BaseDependencyObject::ranking)
            .distinct()
            .filterNotNull()
            .map{it->Ranking.valueOf(it)}
//            .sorted(Comparator.comparing(Ranking::rank).reversed())
            .sortedDescending()  // todo sort by rank descending
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
     concentrate=false;
     
     
     """.trimIndent())

        writeToOutput(out, header)
    }

    /**
     * Write out ranking from the type enumerations. If I were to make this more general, it'd read these in from the file itelf, instead of a hardcoded enum. The output looks like
     * this:{
     *
     *
     * "CDM tables" -> "CDM loaders" -> "CDM views" -> "CDB views" -> "CDB tables"; }
     */
    private fun writeRankingEnumeration(out: DataOutputStream, types: List<Ranking>): List<Ranking> {
        if (doRankings) {
            writeToComment(out, "Ranking Enumeration")
            writeToOutput(out, "node [shape=plaintext,fontname=\"Arial\",fontsize=\"10\"];\n")
            writeToOutput(out, "{ ")

            val line = types.stream()
                .map { type: Ranking -> "\"" + type + '\"' }
                .collect(Collectors.joining(" -> "))

            writeToOutput(out, line)
            writeToOutput(out, " }\n\n")
        }

        return types
    }

    private fun writeObjectDeclarations(objects: Collection<BaseDependencyObject>, out: DataOutputStream) {
        writeToComment(out, "Declarations")
        objects.stream()
            .sorted()
            .forEach { `object`: BaseDependencyObject ->
                val name = `object`.ranking
                val type = Ranking.valueOf(name!!)
                val text = StringBuilder()
                val notes = `object`.notes
                val displayName: String

                if (notes.size == 0) {
                    displayName = `object`.displayName
                }
                else {
                    val displayText = StringBuilder(`object`.displayName)

                    for (note in notes) {
                        displayText.append("\\n").append(note)
                    }

                    displayName = displayText.toString()
                }

                text.append(`object`.name).append(" [label=\"").append(displayName).append('\"')
                text.append(" shape=").append(type.shape)
                text.append(" color=\"").append(type.color).append("\"];\n")

                val outputText = text.toString()
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
                objects.stream()
                    .filter { `object`: BaseDependencyObject -> `object`.ranking == type.name }
                    .sorted()
                    .forEach { `object`: BaseDependencyObject -> writeToOutput(out, '\"'.toString() + `object`.name + "\"; ") }
                writeToOutput(out, "}\n")
            }

            writeToOutput(out, "\n\n")
        }
    }

    private fun writeObjectDependencies(objects: Collection<BaseDependencyObject>, out: DataOutputStream) {
        writeToComment(out, "Dependencies")

        val names: List<String> = objects
            .map{ it.name}
        val lines: MutableList<String> = mutableListOf()

        objects.forEach { dependencyObject ->
            dependencyObject.dependencies
                .filter { names.contains(it) }
                .forEach { lines.add("$dependencyObject -> $it;\n") }
        }

        lines.sorted()
            .forEach { writeToOutput(out, it) }

        writeToOutput(out, "\n\n")
    }

    private fun writeSpouses(objects: Collection<BaseDependencyObject>, out: DataOutputStream) {
        val lines: MutableList<String> = mutableListOf()
        val names = objects
            .map(BaseDependencyObject::name)

        objects
            .filterIsInstance<Person>()
            .forEach { oooo: BaseDependencyObject ->
                (oooo as Person).spouses
                    .stream()
                    .filter(names::contains)
                    .map { spouse -> oooo.name + " -> " + spouse + ";\n" }
                    .forEach(lines::add)
            }

        if (lines.isNotEmpty()) {
            writeToComment(out, "Spouses")
            writeToOutput(out, "edge [color=red,arrowhead=none]\n")
        }

        lines.stream()
            .sorted()
            .forEach { l: String -> writeToOutput(out, l) }
        writeToOutput(out, "\n\n")
    }

    /**
     * method to suppress checked exceptions).
     */
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
