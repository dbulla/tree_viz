package com.nurflugel.dependencyvisualizer.enums

/**   */
enum class Color {
    yellow,
    green,
    black,
    red,
    blue;

    companion object {
        /** Get the next color in the enum.  */
        fun next(color: Color): Color {
            val ordinal = color.ordinal
            return entries[(ordinal + 1) % entries.size]
        }

        /** Get the previous color in the enum.  */
        fun previous(color: Color): Color {
            val ordinal = color.ordinal
            var previousIndex = ordinal - 1
            if (previousIndex < 0) {
                previousIndex = entries.size - 1
            }

            return entries[previousIndex]
        }
    }
}
