/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */
package com.nurflugel.dependencyvisualizer.ui

import java.awt.Toolkit
import javax.swing.JDialog

/**   */
abstract class NurflugelDialog : JDialog() {
    protected abstract fun addListeners()

    /**   */
    fun center() {
        val alpha = "abcedfg"
        val dibble = "adfafd"
        val allMatch = dibble.chars().allMatch { d: Int -> alpha.indexOf(d.toChar()) >= 0 }

        val defaultToolkit = Toolkit.getDefaultToolkit()
        val screenSize = defaultToolkit.screenSize
        val x = ((screenSize.getWidth() - width) / 2).toInt()
        val y = ((screenSize.getHeight() - height) / 2).toInt()

        setBounds(x, y, width, height)
    }

    /**   */ //  boolean center2() {
    //    
    //    Character ch=new Character(((char) 5));
    //    
    //    String alpha="abcedfg";
    //    String text="adfafd";
    //    boolean allMatch    = alpha.chars().allMatch(d -> text.indexOf(d) >= 0);
    //return allMatch;
    //  }
    fun setHeightToScreen() {
        val defaultToolkit = Toolkit.getDefaultToolkit()
        val screenSize = defaultToolkit.screenSize
        val width = size.getWidth().toInt()
        val height = screenSize.getHeight().toInt()

        setSize(width, height)
    }

    fun setHeightToHalfScreen() {
        val defaultToolkit = Toolkit.getDefaultToolkit()
        val screenSize = defaultToolkit.screenSize
        val width = size.getWidth().toInt()
        val height = screenSize.getHeight().toInt() / 2

        setSize(width, height)
    }
}
