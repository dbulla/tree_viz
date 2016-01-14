package com.nurflugel.dependencyvisualizer.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**  */
@SuppressWarnings({ "InstanceVariableUsedBeforeInitialized", "InstanceVariableMayNotBeInitialized" })
public class NoDotDialog extends NurflugelDialog
{
  private File       file;
  private JButton    buttonCancel;
  private JButton    buttonOK;
  private JButton    useTextBoxButton;
  private JPanel     contentPane;
  private JTextField pathTextField;

  /** Creates a new NoDotDialog object. */
  public NoDotDialog(String dotExecutablePath)
  {
    buildDialog(dotExecutablePath);
  }

  protected void buildDialog(String dotExecutablePath)
  {
    setContentPane(contentPane);
    setModal(true);
    getRootPane().setDefaultButton(buttonOK);
    pathTextField.setText(dotExecutablePath);
    addListeners();

    // call onCancel() when cross is clicked
    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
    pack();
    center();
    setVisible(true);
  }

  protected void addListeners()
  {
    buttonOK.addActionListener(e -> onOK());
    useTextBoxButton.addActionListener(e ->
                                       {
                                         file = new File(pathTextField.getText());
                                         dispose();
                                       });
    buttonCancel.addActionListener(e -> onCancel());
    addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent e)
        {
          onCancel();
        }
      });

    // call onCancel() on ESCAPE
    contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
  }

  /**  */
  private void onOK()
  {
    // valueOf your code here
    JFileChooser fileChooser = new JFileChooser();
    int          result      = fileChooser.showOpenDialog(this);

    if (result == JFileChooser.APPROVE_OPTION)
    {
      file = fileChooser.getSelectedFile();
    }

    dispose();
  }

  /**  */
  private void onCancel()
  {
    // valueOf your code here if necessary
    dispose();
  }

  // ------------------------ GETTER/SETTER METHODS ------------------------
  public File getFile()
  {
    return file;
  }

  /**  */
  public static void main(String[] args)
  {
    NoDotDialog dialog = new NoDotDialog("Test message for not finding path");

    dialog.pack();
    dialog.setVisible(true);
    System.exit(0);
  }

  {
    // GUI initializer generated by IntelliJ IDEA GUI Designer
    // >>> IMPORTANT!! <<<
    // DO NOT EDIT OR ADD ANY CODE HERE!
    $$$setupUI$$$();
  }

  /**
   * Method generated by IntelliJ IDEA GUI Designer >>> IMPORTANT!! <<< DO NOT edit this method OR call it in your code!
   *
   * @noinspection  ALL
   */
  private void $$$setupUI$$$()
  {
    contentPane = new JPanel();
    contentPane.setLayout(new GridLayoutManager(3, 1, new Insets(0, 0, 0, 0), -1, -1));

    final JPanel panel1 = new JPanel();

    panel1.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
    contentPane.add(panel1,
                    new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

    final JPanel panel2 = new JPanel();

    panel2.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel2,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

    final JLabel label1 = new JLabel();

    label1.setHorizontalAlignment(0);
    label1.setHorizontalTextPosition(2);
    label1.setText("distribution, or click  \"Cancel\" to exit the application.");
    panel2.add(label1,
               new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                                   GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

    final JLabel label2 = new JLabel();

    label2.setText("Please use the  \"Open Dialog\" button or the text box to select the executable file in the");
    panel2.add(label2,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_FIXED,
                                   GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));

    final JPanel panel3 = new JPanel();

    panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    contentPane.add(panel3,
                    new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

    final Spacer spacer1 = new Spacer();

    panel3.add(spacer1,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_VERTICAL, 1, GridConstraints.SIZEPOLICY_WANT_GROW,
                                   null, null, null, 0, false));
    pathTextField = new JTextField();
    panel3.add(pathTextField,
               new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                                   GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));

    final JPanel panel4 = new JPanel();

    panel4.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    contentPane.add(panel4,
                    new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                        GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    buttonOK = new JButton();
    buttonOK.setText("Open Dialog");
    panel4.add(buttonOK,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    useTextBoxButton = new JButton();
    useTextBoxButton.setText("Use above path");
    panel4.add(useTextBoxButton,
               new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    buttonCancel = new JButton();
    buttonCancel.setText("Cancel");
    panel4.add(buttonCancel,
               new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
  }

  /** @noinspection  ALL */
  public JComponent $$$getRootComponent$$$()
  {
    return contentPane;
  }
}
