package com.nurflugel.dependencyvisualizer.enums

enum class Shape {
    ELLIPSE,
    RECTANGLE,
    HEXAGON,
    TRIANGLE;

    companion object {

        /** Get the next color in the enum.  */
        fun next(shape: Shape): Shape {
            return entries[(shape.ordinal + 1) % entries.size]
        }

        /** Get the next shape in the enum.  */
        fun previous(shape: Shape): Shape {
            val ordinal = shape.ordinal
            var previousIndex = ordinal - 1

            if (previousIndex < 0) {
                previousIndex = entries.size - 1
            }

            return entries[previousIndex]
        }

//        fun first(): Shape {
//            return entries[0]
//        }
//
//        fun last(): Shape {
//            return entries[entries.size - 1]
//        }
    }
}
