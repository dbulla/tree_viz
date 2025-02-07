/**
 * Created by IntelliJ IDEA.
 * User: douglasbullard
 * Date: Dec 2, 2004
 * Time: 8:03:51 PM
 *
 */
package com.nurflugel.dependencyvisualizer.enums

/** Copyright 2005, Nurflugel.com.  */
enum class OutputFormat(
    // for debug only
     val displayLabel: String, // for debug only
     var type: String,
     var extension: String
) {
    // ------------------------------ FIELDS ------------------------------
    Svg("SVG", "svg", ".svg"),
    Png("PNG", "png", ".png"),
    Pdf("PDF", "pdf", ".pdf"),
    Dot("DOT", "dot", ".dot"),
    PdfViaGhostscript("PDF_VIA_GHOSTSCRIPT", "ps", ".ps");

    companion object {
        fun getDefaultExtension(): OutputFormat {
            return Pdf
        }
    }
}
