/**
 * Created by IntelliJ IDEA.
 * User: douglasbullard
 * Date: Dec 2, 2004
 * Time: 8:03:51 PM
 *
 */
package com.nurflugel.dependencyvisualizer.enums;

/** Copyright 2005, Nurflugel.com. */
public enum OutputFormat
{
  // ------------------------------ FIELDS ------------------------------
  Svg              ("SVG", "svg", ".svg"),
  Png              ("PNG", "png", ".png"),
  Pdf              ("PDF", "epdf", ".pdf"),
  PdfViaGhostscript("PDF_VIA_GHOSTSCRIPT", "ps", ".ps");

  private String displayLabel;  // for debug only
  private String extension;     // for debug only
  private String type;          // for debug only

  // --------------------------- CONSTRUCTORS ---------------------------
  /**  */
  OutputFormat(String displayLabel, String type, String extension)
  {
    this.displayLabel = displayLabel;
    this.type         = type;
    this.extension    = extension;
  }
  // -------------------------- OTHER METHODS --------------------------

  /**  */
  public String displayLabel()
  {
    return displayLabel;
  }

  /**  */
  public String extension()
  {
    return extension;
  }

  /**  */
  public String type()
  {
    return type;
  }
}
