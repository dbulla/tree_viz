package com.nurflugel.dependencyvisualizer.enums

enum class Shape {
    box,
    ellipse,
    rectangle,
    hexagon,
    triangle;

    companion object {

        /** Get the next color in the enum.  */
        fun next(shape: Shape): Shape {
            return entries[(shape.ordinal + 1) % entries.size]
        }

        /** Get the next shape in the enum.  */
        fun previous(shape: Shape): Shape {
            val previousIndex = shape.ordinal - 1

            return when {
                previousIndex < 0 -> entries.last()
                else              -> entries[previousIndex]
            }
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
