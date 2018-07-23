/**
 * Application.java - Handles the creation of status GUI and selection of file
 * Begun 07/08/18
 * @author Andrew Eissen
 */
//package recursivedescentparser;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.filechooser.*;
import java.io.*;
import java.util.*;

/**
 * This class is the initializing class of the program, used to both contain the <code>main</code>
 * method and to display a status GUI that provides the user with the ability to select files and
 * begin the parsing process. The class also handles button click events, clearing the status log,
 * toggling token/error details, and prompting the user to select a <code>.txt</code> file with a
 * <code>JFileChooser</code> via the <code>Application.class.selectFile</code> method. That method
 * handles the checking of the file's existence and creates a new <tt>RecursiveDescentParser</tt>
 * class instance in such cases, passing the <code>Scanner</code> results along to the proper
 * <tt>RecursiveDescentParser.class.processScannerContents</tt> method.
 * <br />
 * <br />
 * @see javax.swing
 * @author Andrew Eissen
 */
final class Application {

    // Status GUI-related fields
    private int windowHeight, windowWidth;
    private String windowTitle, fileName, defaultText;
    private JFrame mainFrame;
    private JPanel mainPanel, buttonPanel, logPanel;
    private JLabel leftButtonLabel, rightButtonLabel;
    private JButton fileButton, clearButton;
    private JToggleButton hideDetailsButton;
    private JScrollPane logScrollPane;
    private JTextArea logTextArea;

    /**
     * Standard constructor
     */
    protected Application() {
        this.setWindowHeight(400);
        this.setWindowWidth(600);
        this.setWindowTitle("Recursive Descent Parser");
        this.setDefaultText("Tokens and error messages are logged here.\nDetailed notifications"
            + " may be turned off by pressing 'Hide details'.\n");
        this.assembleStatusGUI();
    }

    // Setters

    /**
     * Setter for <code>windowHeight</code>, status GUI's height attribute
     * @param windowHeight <code>int</code>
     * @return void
     */
    private void setWindowHeight(int windowHeight) {
        this.windowHeight = windowHeight;
    }

    /**
     * Setter for <code>windowWidth</code>, status GUI's width attribute
     * @param windowWidth <code>int</code>
     * @return void
     */
    private void setWindowWidth(int windowWidth) {
        this.windowWidth = windowWidth;
    }

    /**
     * Setter for <code>windowTitle</code>, status GUI's title attribute
     * @param windowTitle <code>String</code>
     * @return void
     */
    private void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    /**
     * Setter for <code>defaultText</code>, status GUI's default masthead text
     * @param defaultText <code>String</code>
     * @return void
     */
    private void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    /**
     * Setter for <code>fileName</code>, user input file name
     * @param fileName <code>String</code>
     * @return void
     */
    private void setFileName(String fileName) {
        this.fileName = fileName;
    }

    // Getters

    /**
     * Getter for <code>windowHeight</code>, status GUI's height attribute
     * @return windowHeight
     */
    private int getWindowHeight() {
        return this.windowHeight;
    }

    /**
     * Getter for <code>windowWidth</code>, status GUI's width attribute
     * @return windowWidth
     */
    private int getWindowWidth() {
        return this.windowWidth;
    }

    /**
     * Getter for <code>windowTitle</code>, status GUI's title attribute
     * @return windowTitle
     */
    private String getWindowTitle() {
        return this.windowTitle;
    }

    /**
     * Getter for <code>defaultText</code>, status GUI's default masthead text
     * @return defaultText
     */
    private String getDefaultText() {
        return this.defaultText;
    }

    // Getters for cross-class usage

    /**
     * Getter for <code>fileName</code>, user input file name
     * @return fileName
     */
    protected String getFileName() {
        return this.fileName;
    }

    /**
     * Getter for <code>hideDetailsButton</code>, a <code>JToggleButton</code>
     * @return hideDetailsButton
     */
    protected JToggleButton getHideDetailsButton() {
        return this.hideDetailsButton;
    }

    /**
     * Getter for <code>mainFrame</code>, main status GUI <code>JFrame</code>
     * @return mainFrame
     */
    protected JFrame getMainFrame() {
        return this.mainFrame;
    }

    // Helper methods

    /**
     * As the name implies, this method is used to log entries in the Status GUI's
     * <code>JTextArea</code> log, displaying important information like tokens, types, and related
     * error messages as they are encountered in the parsing process. This method is called from
     * within this class, and from within a similarly named method belonging to
     * <code>RecursiveDescentParser.class</code>.
     *
     * @param message <code>String</code> message contents
     * @return void
     */
    protected void addLogEntry(String message) {
        this.logTextArea.append(message + "\n");
    }

    /**
     * This method is used to assemble a status GUI that provides the user with options buttons and
     * a handy log of important data. Tokens and types are displayed here, along with lines and any
     * resultant errors that may have been encountered during the parsing process. The option
     * buttons include a button that opens a <code>JFileChooser</code> modal window from which the
     * user may select a properly formatted file, a "Clear messages" button that removes previous
     * log entries from the log panel, and a toggle button that enables the user to switch on a
     * "minimalist mode" bereft of most status messages and all tokens/types.
     *
     * @return void
     */
    private void assembleStatusGUI() {

        // Panel definitions
        this.mainPanel = new JPanel(new BorderLayout());
        this.buttonPanel = new JPanel(new GridLayout(1, 5, 5, 5));
        this.logPanel = new JPanel(new GridLayout(1, 1));

        // Definitions
        this.leftButtonLabel = new JLabel("");
        this.rightButtonLabel = new JLabel("");
        this.fileButton = new JButton("Select file");
        this.clearButton = new JButton("Clear log");
        this.hideDetailsButton = new JToggleButton("Hide details");
        this.logTextArea = new JTextArea(this.getDefaultText());
        this.logScrollPane = new JScrollPane(this.logTextArea);

        // Log text area options
        this.logTextArea.setEditable(false);
        this.logTextArea.setFont(new Font("Monospaced", 0, 11));
        this.logTextArea.setLineWrap(true);

        // File select button handler
        this.fileButton.addActionListener((ActionEvent e) -> {
            this.selectFile();
        });

        // Clear old log button handler
        this.clearButton.addActionListener((ActionEvent e) -> {
            this.logTextArea.setText(this.getDefaultText());
        });

        // Addition to minipanels
        this.buttonPanel.add(this.leftButtonLabel);     // Space-filling label
        this.buttonPanel.add(this.fileButton);          // Select file button (leftmost)
        this.buttonPanel.add(this.clearButton);         // Clear log button (middle)
        this.buttonPanel.add(this.hideDetailsButton);   // Hide details button (rightmost)
        this.buttonPanel.add(this.rightButtonLabel);    // Space-filling label
        this.logPanel.add(this.logScrollPane);          // Main status log

        // Add borders
        this.buttonPanel.setBorder(BorderFactory.createTitledBorder("Options"));
        this.logPanel.setBorder(BorderFactory.createTitledBorder("Status log"));

        // Add minipanels to mainPanel
        this.mainPanel.add(this.buttonPanel, BorderLayout.NORTH);
        this.mainPanel.add(this.logPanel, BorderLayout.CENTER);

        // Placement/sizing details for main JFrame element
        this.mainFrame = new JFrame(this.getWindowTitle());
        this.mainFrame.setContentPane(this.mainPanel);
        this.mainFrame.setSize(this.getWindowWidth(), this.getWindowHeight());
        this.mainFrame.setResizable(false);
        this.mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.mainFrame.setVisible(true);
    }

    /**
     * This method is called from within body of <code>Application.class.assembleStatusGUI</code>
     * whenever the <code>fileButton</code> button is pressed. This method displays a
     * <code>JFileChooser</code> modal window that allows the user to select a properly formatted
     * <code>.txt</code> file from the appropriate directory. Assuming the file exists, the method
     * then creates a new <code>RecursiveDescentParser</code> instance, passing <code>Scanner</code>
     * contents to <code>RecursiveDescentParser.class.processScannerContents</code> for parsing.
     * <br />
     * <br />
     * Much of this method's contents were modified from a similar method used during the author's
     * CMSC 335 SeaPort Project Series projects. A relevant example of such a method may be found
     * <a href="//github.com/andreweissen/SeaPort_Project_4/blob/master/Files/SeaPortProgram.java">
     * here</a>.
     *
     * @return void
     */
    private void selectFile() {

        // Declarations
        File file;
        JFileChooser fileChooser;
        Scanner scannerContents;
        RecursiveDescentParser newParser;

        /**
         * Addition of <code>.txt</code> file-only filter, as per the answer
         * <a href="http://www.stackoverflow.com/questions/15771949">here</a>.
         */
        fileChooser = new JFileChooser(".");
        fileChooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setDialogTitle("Select properly formatted text file");

        if (fileChooser.showOpenDialog(new JFrame()) == JFileChooser.APPROVE_OPTION) {
            try {
                file = fileChooser.getSelectedFile();
                this.addLogEntry("--- " + file.getName() + " ---");

                if (file.exists() && file.length() != 0) {
                    scannerContents = new Scanner(new FileReader(file));
                    this.setFileName(file.getName());

                    // Create new parser, provide with Scanner contents and reference to this class
                    newParser = new RecursiveDescentParser(this);
                    newParser.processScannerContents(scannerContents);
                } else {
                    this.addLogEntry("Error: File '" + file.getName() + "' is empty.");
                }
            } catch (FileNotFoundException ex) {
                this.addLogEntry("Error: No such file found. Please try again.");
            }
        }
    }

    /**
     * The main method simply creates a new <code>Application.class</code> object.
     * @param args <code>String[]</code> command line arguments
     * @return void
     */
    public static void main(String[] args) {
        Application newApplication = new Application();
    }
}