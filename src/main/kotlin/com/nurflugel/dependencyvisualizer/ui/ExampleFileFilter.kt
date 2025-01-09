package com.nurflugel.dependencyvisualizer.ui

import java.io.File
import java.util.*
import javax.swing.filechooser.FileFilter

/**
 * A convenience implementation of FileFilter that filters out all files except for those type extensions that it knows about.
 *
 *
 * Extensions are of the type ".foo", which is typically found on Windows and Unix boxes, but not on Macinthosh. Case is ignored.
 *
 *
 * Example - create a new filter that filerts out all files but gif and jpg image files:
 *
 *
 * JFileChooser chooser = new JFileChooser(); ExampleFileFilter filter = new ExampleFileFilter(new String{"gif", "jpg" }, "JPEG & GIF Images")
 * chooser.addChoosableFileFilter(filter); chooser.showOpenDialog(this);
 *
 * @author   Jeff Dinkins
 * @version  1.10 02/06/02
 */
class ExampleFileFilter() : FileFilter() {
    private var filters: Hashtable<String, ExampleFileFilter>? = null
    private var description: String? = null
    private var fullDescription: String? = null
    private var useExtensionsInDescription = true

    /**
     * Creates a file filter. If no filters are added, then all files are accepted.
     *
     * @see .addExtension
     */
    init {
        filters = Hashtable()
    }

    /**
     * Creates a file filter from the given string array and description. Example: new ExampleFileFilter(String {"gif", "jpg" }, "Gif and JPG Images");
     *
     *
     * Note that the "." before the extension is not needed and will be ignored.
     *
     * @see .addExtension
     */
    /**
     * Creates a file filter from the given string array. Example: new ExampleFileFilter(String {"gif", "jpg" });
     *
     *
     * Note that the "." before the extension is not needed adn will be ignored.
     *
     * @see .addExtension
     */
//    @JvmOverloads
    constructor(filters: Array<String>, description: String? = null) : this() {
        for (filter in filters) {
            // valueOf filters one by one
            addExtension(filter)
        }

        if (description != null) {
            setDescription(description)
        }
    }

    /**
     * Creates a file filter that accepts the given file type. Example: new ExampleFileFilter("jpg", "JPEG Image Images");
     *
     *
     * Note that the "." before the extension is not needed. If provided, it will be ignored.
     *
     * @see .addExtension
     */
    /**
     * Creates a file filter that accepts files with the given extension. Example: new ExampleFileFilter("jpg");
     *
     * @see .addExtension
     */
//    @JvmOverloads
    constructor(extension: String?, description: String? = null) : this() {
        if (extension != null) {
            addExtension(extension)
        }

        if (description != null) {
            setDescription(description)
        }
    }

    /**
     * Adds a filetype "dot" extension to filter against.
     *
     *
     * For example: the following code will create a filter that filters out all files except those that end in ".jpg" and ".tif":
     *
     *
     * ExampleFileFilter filter = new ExampleFileFilter(); filter.addExtension("jpg"); filter.addExtension("tif");
     *
     *
     * Note that the "." before the extension is not needed and will be ignored.
     */
    fun addExtension(extension: String) {
        if (filters == null) {
            filters = Hashtable(5)
        }

        filters!![extension.lowercase(Locale.getDefault())] = this
        fullDescription = null
    }

    /**
     * Sets the human readable description of this filter. For example: filter.setDescription("Gif and JPG Images");
     *
     * @see .setDescription
     *
     * @see .setExtensionListInDescription
     *
     * @see .isExtensionListInDescription
     */
    fun setDescription(description: String?) {
        this.description = description
        fullDescription = null
    }

    // ------------------------ OTHER METHODS ------------------------
    /**
     * Return true if this file should be shown in the directory pane, false if it shouldn't.
     *
     *
     * Files that begin with "." are ignored.
     *
     * @see .getExtension
     *
     * @see FileFilter.accept
     */
    override fun accept(f: File?): Boolean {
        if (f != null) {
            if (f.isDirectory) {
                return true
            }

            val extension = getExtension(f)

            if ((extension != null) && (filters!![getExtension(f)] != null)) {
                return true
            }
        }

        return false
    }

    /**
     * Return the extension portion of the file's name .
     *
     * @see .getExtension
     *
     * @see FileFilter.accept
     */
    private fun getExtension(f: File?): String? {
        if (f != null) {
            val filename = f.name
            val i = filename.lastIndexOf('.')

            if ((i > 0) && (i < (filename.length - 1))) {
                return filename.substring(i + 1).lowercase(Locale.getDefault())
            }
        }

        return null
    }

    /**
     * Returns the human-readable description of this filter. For example: "JPEG and GIF Image Files (*.jpg, *.gif)"
     *
     * @see .setDescription
     *
     * @see .setExtensionListInDescription
     *
     * @see .isExtensionListInDescription
     *
     * @see FileFilter.getDescription
     */
    override fun getDescription(): String {
        if (fullDescription == null) {
            if ((description == null) || isExtensionListInDescription) {
                fullDescription = if (description == null)
                    "("
                else
                    ("$description (")

                // build the description from the extension list
                val extensions = filters!!.keys()

                if (extensions != null) {
                    fullDescription += ("." + extensions.nextElement())

                    while (extensions.hasMoreElements()) {
                        fullDescription += (", " + extensions.nextElement())
                    }
                }

                fullDescription += ")"
            }
            else {
                fullDescription = description
            }
        }

        return fullDescription!!
    }

    private var isExtensionListInDescription: Boolean
        /**
         * Returns whether the extension list (.jpg, .gif, etc) should show up in the human readable description.
         *
         * Only relevant if a description was provided in the constructor or using setDescription();
         *
         * @see .getDescription
         * @see .setDescription
         * @see .setExtensionListInDescription
         */
        get() = useExtensionsInDescription
        /**
         * Determines whether the extension list (.jpg, .gif, etc) should show up in the human readable description.
         *
         *
         * Only relevent if a description was provided in the constructor or using setDescription();
         * @see .getDescription
         * @see .setDescription
         * @see .isExtensionListInDescription
         */
        set(b) {
            useExtensionsInDescription = b
            fullDescription = null
        }

    companion object {
        private const val TYPE_UNKNOWN = "Type Unknown"
        private const val HIDDEN_FILE = "Hidden File"
    }
}
