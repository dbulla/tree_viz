package com.nurflugel.dependencyvisualizer.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.nurflugel.dependencyvisualizer.data.DataHandler;
import com.nurflugel.dependencyvisualizer.data.dataset.BaseDependencyDataSet;
import com.nurflugel.dependencyvisualizer.data.pojos.BaseDependencyObject;
import com.nurflugel.dependencyvisualizer.data.pojos.DependencyObject;
import com.nurflugel.dependencyvisualizer.enums.DirectionalFilter;
import com.nurflugel.dependencyvisualizer.enums.OutputFormat;
import com.nurflugel.dependencyvisualizer.enums.Ranking;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.prefs.Preferences;
import static com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.Down;
import static com.nurflugel.dependencyvisualizer.enums.DirectionalFilter.Up;
import static com.nurflugel.dependencyvisualizer.enums.OutputFormat.Dot;
import static com.nurflugel.dependencyvisualizer.enums.Ranking.clearRankings;
import static java.lang.Boolean.parseBoolean;
import static java.util.stream.Collectors.toList;
import static javax.swing.JFileChooser.APPROVE_OPTION;

/**  */
@SuppressWarnings({
                    "UNUSED_SYMBOL",
                    "InstanceVariableUsedBeforeInitialized",
                    "InstanceVariableMayNotBeInitialized",
                    "CallToRuntimeExec",
                    "UseOfProcessBuilder",
                    "CallToSystemExit",
                    "ResultOfObjectAllocationIgnored"
                  })
public class LoadersUi extends JFrame
{
  /** Use serialVersionUID for interoperability. */
  private static final long     serialVersionUID     = 7606199355832921065L;
  protected static final String LAST_DIR             = "LAST_DIR";
  private static final Logger   logger               = LoggerFactory.getLogger(LoadersUi.class);
  private static final String   DOT_EXECUTABLE       = "dotExecutable";
  private static final String   USE_RANKING          = "userRanking";
  private static final String   FILTER_UP            = "filterUp";
  private static final String   FILTER_DOWN          = "filterDown";
  private static final String   FAMILY_TREE          = "familyTree";
  public String                 version              = "1.0.0";
  private JButton               quitButton;
  private JButton               makeGraphButton;
  private JButton               findDotButton;
  private JButton               loadDatafileButton;
  private JCheckBox             filterDownCheckBox;
  private JCheckBox             filterUpCheckBox;
  private JCheckBox             rankingCheckBox;
  private Preferences           preferences;
  private Cursor                busyCursor           = Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR);
  private Cursor                normalCursor         = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
  private String                dotExecutablePath;
  private String                os                   = System.getProperty("os.name");
  private JPanel                mainPanel;
  private JPanel                filtersPanel;
  private JButton               editDataButton;
  private DataHandler           dataHandler;
  private JButton               newDataButton;
  private JCheckBox             familyTreeCheckBox;
  private JButton               saveDataFileButton;
  private JButton               clearDropdownsButton;

  // private Set<DependencyObject> objects              = new TreeSet<>();
  private BaseDependencyDataSet dataSet;
  private static final String   WINDOWS_DOT_LOCATION = "\"C:\\Program Files\\ATT\\Graphviz\\bin\\dot.exe\"";
  private static final String   OSX_DOT_LOCATION     = "/Applications/Graphviz.app/Contents/MacOS/dot";
  private static final String   MAC_OS               = "Mac OS";
  private static final String   PREVIEW_LOCATION     = "/Applications/Preview.app/Contents/MacOS/Preview";
  private static final String   WINDOWS              = "windows";

  @SuppressWarnings({ "OverridableMethodCallInConstructor" })
  public LoadersUi()
  {
    initializeComponents();
    addListeners();
  }

  private void initializeComponents()
  {
    Container contentPane = getContentPane();

    contentPane.add(mainPanel);
    makeGraphButton.setEnabled(false);
    retrieveSettings();
    pack();
    center();
    setVisible(true);
  }

  private void retrieveSettings()
  {
    preferences       = Preferences.userNodeForPackage(LoadersUi.class);
    dotExecutablePath = preferences.get(DOT_EXECUTABLE, "");
    rankingCheckBox.setSelected(parseBoolean(preferences.get(USE_RANKING, "true")));
    filterUpCheckBox.setSelected(parseBoolean(preferences.get(FILTER_UP, "false")));
    filterDownCheckBox.setSelected(parseBoolean(preferences.get(FILTER_DOWN, "false")));
    familyTreeCheckBox.setSelected(parseBoolean(preferences.get(FAMILY_TREE, "true")));
  }

  /**  */
  @SuppressWarnings({ "NumericCastThatLosesPrecision" })
  private void center()
  {
    Toolkit   defaultToolkit = Toolkit.getDefaultToolkit();
    Dimension screenSize     = defaultToolkit.getScreenSize();
    int       x              = (int) ((screenSize.getWidth() - getWidth()) / 2);
    int       y              = (int) ((screenSize.getHeight() - getHeight()) / 2);
    LoadersUi loadersUi      = this;

    loadersUi.setBounds(x, y, getWidth(), getHeight());
  }

  private void addListeners()
  {
    familyTreeCheckBox.addActionListener(e -> dataSet.setFamilyTree(familyTreeCheckBox.isSelected()));
    quitButton.addActionListener(actionEvent -> doQuitAction());
    makeGraphButton.addActionListener(actionEvent ->
                                      {
                                        try
                                        {
                                          makeGraph();
                                        }
                                        catch (IOException | InterruptedException e)
                                        {
                                          logger.error("Error", e);
                                        }
                                      });
    findDotButton.addActionListener(actionEvent -> findDotExecutablePath());
    loadDatafileButton.addActionListener(actionEvent -> loadDatafile());
    editDataButton.addActionListener(actionEvent -> new DataEditorUI(dataSet));
    saveDataFileButton.addActionListener(e -> dataHandler.saveDataset());
    addWindowListener(new WindowAdapter()
      {
        @Override
        public void windowClosing(WindowEvent e)
        {
          super.windowClosing(e);
          doQuitAction();
        }
      });
  }

  private void makeGraph() throws IOException, InterruptedException
  {
    dataHandler.initialize();
    dataHandler.setDirectionalFilters(getDirectionalFilters());
    dataHandler.setIsRanking(rankingCheckBox.isSelected());
    dataHandler.setKeyObjectsToFilterOn(getKeyObjects());
    dataHandler.setTypesFilters(new ArrayList<Ranking>());

    File dotFile = dataHandler.doIt();
    // String outputFilePath = convertDataFile(dotFile);

    // showImage(outputFilePath);
    showImage(dotFile.getAbsolutePath());
  }

  private List<DirectionalFilter> getDirectionalFilters()
  {
    List<DirectionalFilter> filters = new ArrayList<>();

    if (filterUpCheckBox.isSelected() && filterDownCheckBox.isSelected())
    {
      return filters;
    }

    if (filterUpCheckBox.isSelected())
    {
      filters.add(Up);
    }

    if (filterDownCheckBox.isSelected())
    {
      filters.add(Down);
    }

    return filters;
  }

  @SuppressWarnings({ "CastConflictsWithInstanceof" })
  private List<BaseDependencyObject> getKeyObjects()
  {
    List<BaseDependencyObject> keyObjects = new ArrayList<>();

    for (Component component : filtersPanel.getComponents())
    {
      if (component instanceof JPanel)
      {
        Component[] panelComponents = ((Container) component).getComponents();

        for (Component panelComponent : panelComponents)
        {
          if (panelComponent instanceof JComboBox)
          {
            getValueFromDropdown(keyObjects, (JComboBox) panelComponent);
          }
        }
      }
    }

    return keyObjects;
  }

  private void getValueFromDropdown(List<BaseDependencyObject> keyObjects, JComboBox comboBox)
  {
    BaseDependencyObject selectedItem = (BaseDependencyObject) comboBox.getSelectedItem();

    if ((selectedItem != null) && !selectedItem.getName().isEmpty())
    {
      keyObjects.add(selectedItem);
    }
  }

  private String convertDataFile(File dotFile) throws IOException, InterruptedException
  {
    String outputFileName = getOutputFileName(dotFile, getOutputFormat().extension());
    File   outputFile     = new File(dotFile.getParent(), outputFileName);
    File   parentFile     = outputFile.getParentFile();
    String dotFilePath    = dotFile.getAbsolutePath();
    String outputFilePath = outputFile.getAbsolutePath();

    if (outputFile.exists())
    {
      if (logger.isDebugEnabled())
      {
        logger.debug("Deleting existing version of " + outputFilePath);
      }

      outputFile.delete();  // delete the file before generating it if it exists
    }

    if (StringUtils.isEmpty(dotExecutablePath))
    {
      findDotExecutablePath();
    }

    String outputFormat = getOutputFormat().type();
    long   start        = new Date().getTime();

    if (isWindows())
    {
      String quote = isWindows() ? "\""
                                 : "";
      String dot     = quote + dotExecutablePath + quote;
      String output  = " -o" + quote + outputFilePath + quote;
      String command = dot + " -T" + outputFormat + ' ' + quote + dotFilePath + quote + output;

      if (logger.isDebugEnabled())
      {
        logger.debug("Command to run: " + command + ", parent file is " + parentFile.getPath());
      }

      Runtime runtime = Runtime.getRuntime();

      runtime.exec(command).waitFor();
    }
    else
    {
      String[] command = { dotExecutablePath, "-T" + outputFormat, dotFilePath, "-o" + outputFilePath };

      if (logger.isDebugEnabled())
      {
        logger.debug("Command to run: " + concatenate(command) + ", parent file is " + parentFile.getPath());
      }

      Runtime runtime = Runtime.getRuntime();

      runtime.exec(command).waitFor();
    }

    long end = new Date().getTime();

    if (logger.isDebugEnabled())
    {
      logger.debug("Took " + (end - start) + " milliseconds to generate graphic");
    }

    return outputFilePath;
  }

  /** Takes someting like build.dot and returns build.Png. */
  private String getOutputFileName(File dotFile, String outputExtension)
  {
    String results = dotFile.getName();
    int    index   = results.indexOf(".dot");

    results = results.substring(0, index) + outputExtension;

    return results;
  }

  /**  */
  private boolean isWindows()
  {
    return os.toLowerCase().startsWith(WINDOWS);
  }

  /**  */
  private String concatenate(String[] command)
  {
    StringBuilder stringBuffer = new StringBuilder();

    for (String s : command)
    {
      stringBuffer.append(' ');
      stringBuffer.append(s);
    }

    return stringBuffer.toString();
  }

  private void showImage(String outputFilePath)
  {
    try
    {
      List<String> commandList = new ArrayList<>();

      if (isWindows())
      {
        commandList.add("cmd.exe");
        commandList.add("/c");
        commandList.add(outputFilePath);
      }
      else if (isOsX())
      {
        // commandList.add(PREVIEW_LOCATION);
        commandList.add("open");

        String delimitedOutputFilePath = outputFilePath;

        // String delimitedOutputFilePath = StringUtils.replace(outputFilePath, " ", "\\ ");
        commandList.add(delimitedOutputFilePath);
      }

      String[] command = commandList.toArray(new String[commandList.size()]);

      if (logger.isDebugEnabled())
      {
        logger.debug("Command to run: " + concatenate(command));
      }

      Process     process     = new ProcessBuilder(commandList).start();
      InputStream errorStream = process.getErrorStream();
    }
    catch (Exception e)
    {
      logger.error("Error", e);
    }
  }

  /**  */
  private boolean isOsX()
  {
    return os.toLowerCase().startsWith("mac os");
  }

  /**  */
  private void findDotExecutablePath()
  {
    dotExecutablePath = preferences.get(DOT_EXECUTABLE, "");

    if ((dotExecutablePath == null) || dotExecutablePath.isEmpty())
    {
      if (os.startsWith(MAC_OS))
      {
        dotExecutablePath = OSX_DOT_LOCATION;
      }
      else  // if (os.toLowerCase().startsWith("windows"))
      {
        dotExecutablePath = WINDOWS_DOT_LOCATION;
      }
    }

    // Create a file chooser
    NoDotDialog dialog            = new NoDotDialog(dotExecutablePath);
    File        dotExecutableFile = dialog.getFile();

    if (dotExecutableFile != null)
    {
      dotExecutablePath = dotExecutableFile.getAbsolutePath();
      preferences.put(DOT_EXECUTABLE, dotExecutablePath);
    }
    else
    {
      JOptionPane.showMessageDialog(this,
                                    "Sorry, this program can't run without the GraphViz installation.\n"
                                      + "  Please install that and try again");
      doQuitAction();
    }
  }

  private void loadDatafile()
  {
    // load data
    setCursor(busyCursor);

    JFileChooser      fileChooser = new JFileChooser();
    ExampleFileFilter filter      = new ExampleFileFilter();

    filter.addExtension("txt");
    filter.addExtension("json");
    filter.setDescription("data files");
    fileChooser.setFileFilter(filter);

    String lastDir = preferences.get(LAST_DIR, "");

    if (lastDir != null)
    {
      fileChooser.setCurrentDirectory(new File(lastDir));
    }

    fileChooser.setMultiSelectionEnabled(false);

    int returnVal = fileChooser.showOpenDialog(this);

    if (returnVal == APPROVE_OPTION)
    {
      File selectedFile = fileChooser.getSelectedFile();

      preferences.put(LAST_DIR, selectedFile.getParent());
      makeGraphButton.setEnabled(true);
      clearRankings();
      dataHandler = new DataHandler(selectedFile);
      dataHandler.loadDataset();
      dataSet = dataHandler.getDataset();
      familyTreeCheckBox.setSelected(dataSet.isFamilyTree());
      populateDropdowns();
      editDataButton.setEnabled(true);
      saveDataFileButton.setEnabled(true);
      makeGraphButton.setEnabled(true);
    }

    pack();
    center();
    setCursor(normalCursor);
  }

  private void populateDropdowns()
  {
    List<Ranking> shapeAttributeses = Ranking.values();

    filtersPanel.removeAll();
    filtersPanel.setLayout(new GridLayout((shapeAttributeses.size() / 2) + 1, 2));

    for (Ranking type : shapeAttributeses)
    {
      BaseDependencyObject[] filteredObjects = getObjectsForType(type);
      JComboBox              comboBox        = new JComboBox(filteredObjects);
      JPanel                 borderPanel     = new JPanel();
      Border                 border          = BorderFactory.createTitledBorder(new EtchedBorder(), type.getName());

      borderPanel.setBorder(border);
      borderPanel.add(comboBox);
      filtersPanel.add(borderPanel);
    }
  }

  private BaseDependencyObject[] getObjectsForType(Ranking type)
  {
    List<BaseDependencyObject> filteredObjects = new ArrayList<>();

    filteredObjects.add(new DependencyObject("", type.getName()));
    filteredObjects.addAll(dataSet.getObjects()
                             .filter(dependencyObject -> dependencyObject.getRanking().equals(type.getName()))
                             .collect(toList()));

    return filteredObjects.toArray(new BaseDependencyObject[filteredObjects.size()]);
  }

  private void doQuitAction()
  {
    saveSettings();
    System.exit(0);
  }

  private void saveSettings()
  {
    preferences.putBoolean(USE_RANKING, rankingCheckBox.isSelected());
    preferences.putBoolean(FILTER_UP, filterUpCheckBox.isSelected());
    preferences.putBoolean(FILTER_DOWN, filterDownCheckBox.isSelected());
    preferences.putBoolean(FAMILY_TREE, familyTreeCheckBox.isSelected());
  }

  private OutputFormat getOutputFormat()
  {
    if (isOsX())
    {
      return Dot;
    }

    return Dot;
  }

  private void populateDropdown(JComboBox comboBox, Ranking type)
  {
    List<BaseDependencyObject> dropdownList = new ArrayList<>();

    dropdownList.add(new DependencyObject("", type.getName()));
    dropdownList.addAll(dataSet.getObjects()
                          .filter(object -> object.getRanking().equals(type.getName()))
                          .collect(toList()));

    BaseDependencyObject[] loaderObjects = dropdownList.toArray(new BaseDependencyObject[dropdownList.size()]);

    // Arrays.sort(loaderObjects);
    comboBox.setModel(new DefaultComboBoxModel(loaderObjects));
  }

  /**  */
  private void setDefaultDotLocation()
  {
    dotExecutablePath = preferences.get(DOT_EXECUTABLE, "");

    if ((dotExecutablePath == null) || dotExecutablePath.isEmpty())
    {
      if (os.startsWith(MAC_OS))
      {
        dotExecutablePath = OSX_DOT_LOCATION;
      }
      else  // if (os.toLowerCase().startsWith("windows"))
      {
        dotExecutablePath = WINDOWS_DOT_LOCATION;
      }
    }
  }

  /**
   * Get the dot executable path if it already exists in Preferences, or is intalled. If not easily findable, as the user where the hell he put it.
   */
  public String getDotExecutablePath()
  {
    return dotExecutablePath;
  }

  public static void main(String[] args)
  {
    LoadersUi ui = new LoadersUi();
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
    mainPanel = new JPanel();
    mainPanel.setLayout(new GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
    filtersPanel = new JPanel();
    filtersPanel.setLayout(new GridLayoutManager(4, 2, new Insets(0, 0, 0, 0), -1, -1));
    mainPanel.add(filtersPanel,
                  new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(200, -1), null, null,
                                      0, false));
    filtersPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filter Critera (none=show all)"));

    final JPanel panel1 = new JPanel();

    panel1.setLayout(new GridLayoutManager(4, 1, new Insets(0, 0, 0, 0), -1, -1));
    mainPanel.add(panel1,
                  new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                      GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

    final JPanel panel2 = new JPanel();

    panel2.setLayout(new GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel2,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

    final Spacer spacer1 = new Spacer();

    panel2.add(spacer1,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                                   1, null, null, null, 0, false));

    final JPanel panel3 = new JPanel();

    panel3.setLayout(new GridLayoutManager(2, 1, new Insets(0, 0, 0, 0), -1, -1));
    panel2.add(panel3,
               new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(200, -1), null, null, 0,
                                   false));
    panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Filter Direction (none = both)"));
    filterUpCheckBox = new JCheckBox();
    filterUpCheckBox.setText("Filter up on selected");
    panel3.add(filterUpCheckBox,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    filterDownCheckBox = new JCheckBox();
    filterDownCheckBox.setText("Filter down on selected");
    panel3.add(filterDownCheckBox,
               new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));

    final Spacer spacer2 = new Spacer();

    panel2.add(spacer2,
               new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                                   1, null, null, null, 0, false));

    final JPanel panel4 = new JPanel();

    panel4.setLayout(new GridLayoutManager(2, 3, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel4,
               new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));

    final Spacer spacer3 = new Spacer();

    panel4.add(spacer3,
               new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                                   1, null, null, null, 0, false));
    rankingCheckBox = new JCheckBox();
    rankingCheckBox.setSelected(true);
    rankingCheckBox.setText("Use rankings");
    rankingCheckBox.setToolTipText("This groups the graph into horizontal layers, where all peers are in the same  layer");
    panel4.add(rankingCheckBox,
               new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));

    final Spacer spacer4 = new Spacer();

    panel4.add(spacer4,
               new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW,
                                   1, null, null, null, 0, false));
    familyTreeCheckBox = new JCheckBox();
    familyTreeCheckBox.setText("Family Tree");
    familyTreeCheckBox.setToolTipText("is this a family tree, or a data graph?  Family trees can include concepts like spouses, births, deaths, etc.");
    panel4.add(familyTreeCheckBox,
               new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));

    final JPanel panel5 = new JPanel();

    panel5.setLayout(new GridLayoutManager(5, 2, new Insets(0, 0, 0, 0), -1, -1));
    panel1.add(panel5,
               new GridConstraints(3, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
    loadDatafileButton = new JButton();
    loadDatafileButton.setText("Load Data file");
    loadDatafileButton.setToolTipText("Load a data set from file on disk");
    panel5.add(loadDatafileButton,
               new GridConstraints(2, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    findDotButton = new JButton();
    findDotButton.setText("Find Dot");
    findDotButton.setToolTipText("If Dot is in a non-standard location, click this to get a dialog to find it");
    panel5.add(findDotButton,
               new GridConstraints(2, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    makeGraphButton = new JButton();
    makeGraphButton.setText("Make Graph");
    panel5.add(makeGraphButton,
               new GridConstraints(4, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    quitButton = new JButton();
    quitButton.setText("Quit");
    panel5.add(quitButton,
               new GridConstraints(4, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    editDataButton = new JButton();
    editDataButton.setText("Edit/Add Data");
    editDataButton.setToolTipText("Edit or add to an existing data file");
    panel5.add(editDataButton,
               new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    newDataButton = new JButton();
    newDataButton.setText("New Data file");
    newDataButton.setToolTipText("Create a new data file");
    panel5.add(newDataButton,
               new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
    saveDataFileButton = new JButton();
    saveDataFileButton.setText("Save Data file");
    saveDataFileButton.setToolTipText("Save open dataset to disk");
    panel5.add(saveDataFileButton,
               new GridConstraints(3, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL,
                                   GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED,
                                   null, null, null, 0, false));
  }

  /** @noinspection  ALL */
  public JComponent $$$getRootComponent$$$()
  {
    return mainPanel;
  }
}
