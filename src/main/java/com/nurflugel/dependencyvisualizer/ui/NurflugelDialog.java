/*
 * Copyright (c) 2006, Your Corporation. All Rights Reserved.
 */
package com.nurflugel.dependencyvisualizer.ui;

import javax.swing.*;
import java.awt.*;

/**  */
@SuppressWarnings({ "AbstractClassExtendsConcreteClass" })
public abstract class NurflugelDialog extends JDialog {
  protected abstract void addListeners();

  /**  */
  void center() {
    Toolkit   defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize     = defaultToolkit.getScreenSize();
    int       x              = (int) ((screenSize.getWidth() - getWidth()) / 2);
    int       y              = (int) ((screenSize.getHeight() - getHeight()) / 2);

    setBounds(x, y, getWidth(), getHeight());
  }

  void setHeightToScreen() {
    Toolkit   defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize     = defaultToolkit.getScreenSize();
    int       width          = (int) getSize().getWidth();
    int       height         = (int) screenSize.getHeight();

    setSize(width, height);
  }

  void setHeightToHalfScreen() {
    Toolkit   defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize     = defaultToolkit.getScreenSize();
    int       width          = (int) getSize().getWidth();
    int       height         = (int) screenSize.getHeight() / 2;

    setSize(width, height);
  }
}
