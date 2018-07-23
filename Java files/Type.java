/**
 * Type.java - Enum for <code>Token</code> types
 * Begun 07/08/18
 * @author Andrew Eissen
 */
//package recursivedescentparser;

/**
 * This enum class file is used to properly categorize the different types of assorted token items
 * that may be encountered within the chosen <code>.txt</code> file selected by the user. The idea
 * of using an <code>enum</code> was, like many pieces of code within the program, derived from the
 * CMSC 330 "C Program Formatter Written in Java" module. All enum types are listed alphabetically
 * and provided with a small summary denoting their purpose within the body of the
 * <code>RecursiveDescentParser.class</code> instance.
 * <br />
 * <br />
 * @author Andrew Eissen
 */
enum Type {
    BUTTON,         // JButton
    COLON,          // Ends Layouts
    COMMA,          // Used between dimensions (numbers)
    END,            // Used to denote end of widgets, GUI
    EOF,            // End of File
    FLOW,           // FlowLayout
    GRID,           // GridLayout
    GROUP,          // Radio buttons grouping
    LABEL,          // JLabel
    LAYOUT,         // Keyword used to begin layout
    LPAREN,         // Open parenthesis
    NUMBER,         // Integer (rows, cols, gaps, dimensions)
    PANEL,          // JPanel subdivision
    PERIOD,         // Used to end file
    RADIO,          // Radio button
    RPAREN,         // Close parenthesis
    SEMICOLON,      // Used to end lines
    STRING,         // Usually a title; placed between quotes
    TEXTFIELD,      // JTextField
    UNKNOWN,        // None of the options offered
    WIDGET,         // Catchall term for widgets (for error messages)
    WINDOW          // Beginning keyword
}