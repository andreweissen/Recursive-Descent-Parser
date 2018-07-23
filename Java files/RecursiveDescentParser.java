/**
 * RecursiveDescentParser.java - Converts file contents into tokens and parses a GUI accordingly
 * Begun 07/08/18
 * @author Andrew Eissen
 */
//package recursivedescentparser;

import java.awt.*;
import javax.swing.*;
import java.util.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * This class is the main class of the program, used as a lexer and a parser translating the
 * <code>Scanner</code> contents of the user-input <code>.txt</code> file into a working
 * <code>Swing</code> GUI.
 * <br />
 * <br />
 * The original "first drafts" of this class were very heavily reliant upon many duplicate levels of
 * nested <code>if...else</code> statement blocks. This approach was naturally very messy and
 * difficult to trace by hand, with many instances of copy/pasted code and duplicate content
 * appearing in the various methods. Through the application of Java principles like reflection,
 * code consolidation in methods, and general cleanup, the class's major methods were improved and
 * made significantly more readable than before. While several major methods included herein do make
 * use of extended <code>if...else</code> statements to determine if tokens are arranged in
 * grammatically permissible order, they are now significantly easier to read. However, the author
 * wishes this assignment had been like CMSC 335's SeaPort project series, which stretched over the
 * length of the class and provided the author with time to research and implement better designs.
 * <br />
 * <br />
 * @see javax.swing
 * @author Andrew Eissen
 */
final class RecursiveDescentParser {

    /*
     * Class table of contents
     * - Setters                                    (line 74)
     * - Getters                                    (line 103)
     * - Lexer
     *   - Lexer methods                            (line 129)
     *   - Lexer helper methods                     (line 220)
     * - Parser
     *   - Printing/logging helper methods          (line 341)
     *   - Token-related getter helper methods      (line 438)
     *   - Format progression-checking methods      (line 491)
     *   - Reflection methods (for recursion)       (line 878)
     *   - Reflection helper methods                (line 1014)
     */

    // Declarations
    private Application parent;
    private ArrayList<Token> tokensList;
    private int tokenIndex;
    private boolean isFirstErrorFound;
    private JDialog resultsDialog;
    private ButtonGroup radioGroup;
    private Type currentTokenType;
    private Container currentElement;

    /**
     * Standard constructor
     * @param parent <code>Application</code> that created class instance
     */
    protected RecursiveDescentParser(Application parent) {
        this.setParent(parent);
        this.setTokensList(new ArrayList<>());
        this.setIsFirstErrorFound(false);
        this.tokenIndex = 0;
    }

    // Setters

    /**
     * Setter for <code>parent</code>
     * @param parent <code>Application</code>
     * @return void
     */
    private void setParent(Application parent) {
        this.parent = parent;
    }

    /**
     * Setter for <code>tokensList</code>
     * @param tokensList <code>ArrayList</code>
     * @return void
     */
    private void setTokensList(ArrayList<Token> tokensList) {
        this.tokensList = tokensList;
    }

    /**
     * Setter for <code>isFirstErrorFound</code>
     * @param isFirstErrorFound <code>boolean</code>
     * @return void
     */
    private void setIsFirstErrorFound(boolean isFirstErrorFound) {
        this.isFirstErrorFound = isFirstErrorFound;
    }

    // Getters

    /**
     * Getter for <code>parent</code>
     * @return parent
     */
    private Application getParent() {
        return this.parent;
    }

    /**
     * Getter for <code>tokensList</code>
     * @return tokensList
     */
    private ArrayList<Token> getTokensList() {
        return this.tokensList;
    }

    /**
     * Getter for <code>isFirstErrorFound</code>
     * @return isFirstErrorFound
     */
    private boolean getIsFirstErrorFound() {
        return this.isFirstErrorFound;
    }

    // Lexer methods

    /**
     * The main lexer method, this method is called within <code>Application.class.selectFile</code>
     * and passed the resultant <code>Scanner</code> contents from the user-selected
     * <code>.txt</code> file.
     * <br />
     * <br />
     * Originally, this method did not examine <code>Scanner</code> lines character by character,
     * but rather made use of some intense regular expressions to split the line <code>String</code>
     * into smaller <code>String</code> tokens. However, while this worked in many cases, it still
     * produced errors and required expensive comparisons to <code>String</code>s within
     * <code>switch</code> statement blocks. Eventually, the author simply removed this approach in
     * favor of a cheaper character-by-character approach. If the author had more time to perfect
     * the program, further work on the regex method would have likely been undertaken.
     * <br />
     * <br />
     * Furthermore, it should be noted that since the grammar itself made no mention of the quote
     * character being a token or non-terminal, the author has not included it in the list of token
     * <code>Type</code>s, nor used it for any purpose than denoting the beginning and ending of a
     * <code>String</code> whenever encountered in the file contents. The program output does not
     * change as a result, and cases wherein a missing quote character is evidenced in the file are
     * still properly caught and reported in the console/log as errors.
     *
     * @param scannerContents <code>Scanner</code> contents from text file
     * @return void
     */
    protected void processScannerContents(Scanner scannerContents) {

        // Declarations
        String scannedLine, compositeString;
        ArrayList<Token> tokens;
        boolean isWithinQuotes;
        char[] splitLine;
        int lineCounter;
        Token newToken;
        Type tokenType;

        // Definitions
        tokens = this.getTokensList();
        lineCounter = 0;

        while (scannerContents.hasNextLine()) {

            // Definitions
            lineCounter++;
            scannedLine = scannerContents.nextLine().trim();
            compositeString = "";
            isWithinQuotes = false;
            splitLine = scannedLine.toCharArray();

            for (char character : splitLine) {
                if (isWithinQuotes) { // if part of a String
                    if (character == '\"') {
                        newToken = new Token(Type.STRING, compositeString, lineCounter);
                        tokens.add(newToken);
                        compositeString = "";
                    } else {
                        compositeString += character;
                    }
                } else {
                    if ("(),:;.".indexOf(character) != -1) {
                        // Clear out compositeString and make its contents a new Token
                        compositeString = this.createNewToken(compositeString.trim(), lineCounter);

                        // Make new Type symbol a Token as well
                        tokenType = this.determineSymbol(character);
                        newToken = new Token(tokenType, String.valueOf(character), lineCounter);
                        tokens.add(newToken);
                    } else if (Character.isWhitespace(character) &&
                            compositeString.trim().length() > 0) {
                        compositeString = this.createNewToken(compositeString, lineCounter);
                    } else {
                        if (character != '\"') {
                            // Handle excess spacing not in Strings
                            compositeString = compositeString.trim() + character;
                        }
                    }
                }

                if (character == '\"') {
                    isWithinQuotes = !isWithinQuotes;
                }
            }
            this.createNewToken(compositeString, lineCounter);
        }

        this.printArrayListToConsole();
        this.parseContents();
    }

    // Lexer helper methods

    /**
     * This helper method was created to simplify three cases of copy/pasta in the above method,
     * <code>RecursiveDescentParser.class.processScannerContents</code>. Rather than reuse the same
     * code three times, this method was added to reduce such instances and enhance readability. It
     * determines token's <code>Type</code> and assembles a new <code>Token</code> instance if the
     * included parameter <code>String</code> is not empty. It returns an empty <code>String</code>
     * in all cases to help clear the above <code>compositeString</code> field.
     *
     * @param composite <code>String</code> <code>char</code> composite word
     * @param lineCounter <code>int</code> line counter for use in status GUI log
     * @return empty <code>String</code>
     */
    private String createNewToken(String composite, int lineCounter) {

        // Declarations
        Type tokenType;
        Token newToken;

        composite = composite.trim();
        if (composite.length() > 0) {
            tokenType = this.determineType(composite);
            newToken = new Token(tokenType, composite, lineCounter);
            this.getTokensList().add(newToken);
        }
        return "";
    }

    /**
     * This method, like that below it, has basically been lifted from the CMSC 335 module entitled
     * "C Program Formatter Written in Java." It is used to determine whether or not the character
     * in question is one of the permitted grammatical symbols. If a match is found, the
     * <code>Type</code> in question is returned, otherwise a <code>Type</code> of
     * <code>Type.UNKNOWN</code> is returned.
     *
     * @param character <code>char</code>
     * @return <code>Type</code>
     */
    private Type determineSymbol(char character) {
        switch(character) {
            case ':':
                return Type.COLON;
            case ',':
                return Type.COMMA;
            case '(':
                return Type.LPAREN;
            case '.':
                return Type.PERIOD;
            case ')':
                return Type.RPAREN;
            case ';':
                return Type.SEMICOLON;
            default:
                return Type.UNKNOWN;
        }
    }

    /**
     * This method, like that above it, is largely lifted from the CMSC 335 module entitled
     * "C Program Formatter Written in Java." Like the aforementioned method, this method is used to
     * determine which <code>Type</code> the included assembled token is, which is then returned
     * from the <code>switch</code> body. If the token in question is not one of the types, the
     * token is checked to see if it is an integer, in which case <code>Type.NUMBER</code> type is
     * returned instead. Else, <code>Type.UNKNOWN</code> is returned.
     *
     * @param token <code>String</code> assembled token
     * @return <code>Type</code>
     */
    private Type determineType(String token) {
        switch(token.charAt(0)) {
            case 'B':
                if (token.equals("Button")) {
                    return Type.BUTTON;
                }
            case 'E':
                if (token.equals("End")) {
                    return Type.END;
                }
            case 'F':
                if (token.equals("Flow")) {
                    return Type.FLOW;
                }
            case 'G':
                if (token.equals("Grid")) {
                    return Type.GRID;
                } else if (token.equals("Group")) {
                    return Type.GROUP;
                }
            case 'L':
                if (token.equals("Label")) {
                    return Type.LABEL;
                } else if (token.equals("Layout")) {
                    return Type.LAYOUT;
                }
            case 'P':
                if (token.equals("Panel")) {
                    return Type.PANEL;
                }
            case 'R':
                if (token.equals("Radio")) {
                    return Type.RADIO;
                }
            case 'T':
                if (token.equals("Textfield")) {
                    return Type.TEXTFIELD;
                }
            case 'W':
                if (token.equals("Window")) {
                    return Type.WINDOW;
                }
            default:
                try { // Check if integer
                    Integer.parseInt(token);
                    return Type.NUMBER;
                } catch (NumberFormatException ex) {
                    return Type.UNKNOWN;
                }
        }
    }

    // Printing/logging helper methods

    /**
     * This helper method simply accepts a <code>String</code> log message and calls the parent
     * <code>Application.class</code> instance's <code>Application.class.addLogEntry</code> method,
     * passing along the message and enabling it to be posted to the log. Method is used on its own
     * and in the below derivative <code>RecursiveDescentParser.class.logErrorMessage</code> methods
     * and friends.
     *
     * @param message <code>String</code>
     * @return void
     */
    private void addLogEntry(String message) {
        this.getParent().addLogEntry(message);
    }

    /**
     * This method makes use of that above it to both log error-specific messages in the user GUI
     * status log and return the most commonly used value of <code>false</code>, used in the methods
     * below to exit from <code>if...else</code> blocks. This method also logs the
     * <code>String</code> representation of the calling method to aid in debugging purposes after
     * the author became confused as to the specific location of the logged error messages. The
     * message in question is only logged if the first error has not been found yet (as per project
     * rubric requirement to report only the first error) and only adds a method name if the "Hide
     * details" button remains unselected.
     *
     * @param expected <code>Type</code> expected from the <code>if</code> statement
     * @param encountered <code>Type</code> that appeared instead of expected
     * @param lineNumber <code>int</code> representing token's position in file
     * @param methodName <code>String</code> representation of calling method's name
     * @return false <code>boolean</code> always returns false to aid in method exiting below
     */
    private boolean logErrorMessage(Type expected, Type encountered, int lineNumber,
            String methodName) {

        // Declaration
        String errorMessage;

        if (!this.getIsFirstErrorFound()) {
            errorMessage = "Error: Expected " + expected + ", encountered " + encountered
                + " (line " + lineNumber + ")";
            if (!this.getParent().getHideDetailsButton().isSelected()) {
                errorMessage += " [" + methodName + "]";
            }
            this.addLogEntry(errorMessage);
            this.setIsFirstErrorFound(true);
        }

        return false;
    }

    /**
     * This method is the two parameter overloaded variation of the method included above. This is
     * the lesser used of the two, only used in certain messages that break the usual "Expected X,
     * encountered Y" progression. The message in question is only logged if the first error has not
     * been found yet (as per project rubric requirement to report only first error) and only adds a
     * method name if the "Hide details" button remains unselected. As with its other variation, the
     * method returns <code>false</code> regardless of the messages logged to aid in exiting from
     * other methods.
     *
     * @param customMessage <code>String</code> error message
     * @param lineNumber <code>int</code> representing token's position in file
     * @param methodName <code>String</code> name of method in which this method is called
     * @return false <code>boolean</code> always returns false to aid in method exiting
     */
    private boolean logErrorMessage(String customMessage, int lineNumber, String methodName) {
        if (!this.getIsFirstErrorFound()) {
            customMessage += " (line " + lineNumber + ")";
            if (!this.getParent().getHideDetailsButton().isSelected()) {
                customMessage += " [" + methodName + "]";
            }
            this.addLogEntry(customMessage);
            this.setIsFirstErrorFound(true);
        }

        return false;
    }

    /**
     * This method is simply a helper method used to mass-print all the various <code>Token</code>s
     * to the Status GUI's log. It is formatted in such a way that each token's individual
     * <code>Type</code> is displayed prior to that token's <code>String</code> value to allow for
     * easy debugging and code tracking. Details are displayed only if the "Hide details" button has
     * not been pressed in the user status GUI.
     *
     * @return void
     */
    private void printArrayListToConsole() {
        if (!this.getParent().getHideDetailsButton().isSelected()) {
            this.getTokensList().forEach((entry) -> {
                String details = "Line " + entry.getLineNumber() + ": " + entry.getType() + " -> "
                    + entry.getToken();
                this.addLogEntry(details);
            });
        }
    }

    // Token-related getter helper methods

    /**
     * This is one of four parser helper methods used to progress through the <code>ArrayList</code>
     * tokens listing at <code>RecursiveDescentParser.class.tokensList</code>. This particular
     * method returns a <code>Type</code> corresponding to the next token to be viewed.
     *
     * @return <code>Type</code>
     */
    private Type getNextTokenType() {
        if (this.tokenIndex == this.getTokensList().size()) {
            return Type.EOF; // End of file, precaution
        } else {
            return this.getTokensList().get(this.tokenIndex++).getType();
        }
    }

    /**
     * This is one of four parser helper methods used to progress through the <code>ArrayList</code>
     * tokens listing at <code>RecursiveDescentParser.class.tokensList</code>. This particular
     * method returns the <code>Type</code> corresponding to the previous (technically current)
     * token in the listing. This is used primarily in error messages to aid in debugging.
     *
     * @return <code>Type</code>
     */
    private Type getTokenType() {
        return this.getTokensList().get(this.tokenIndex - 1).getType();
    }

    /**
     * This is one of four parser helper methods used to progress through the <code>ArrayList</code>
     * tokens listing at <code>RecursiveDescentParser.class.tokensList</code>. This method is used
     * to return the specific line number at which the previous (technically current) token was
     * found in the text file, used for debugging purposes in the status log.
     *
     * @return <code>int</code> line number
     */
    private int getTokenLineNumber() {
        return this.getTokensList().get(this.tokenIndex - 1).getLineNumber();
    }

    /**
     * This is one of four parser helper methods used to progress through the <code>ArrayList</code>
     * tokens listing at <code>RecursiveDescentParser.class.tokensList</code>. This particular
     * method returns the <code>String</code> lexeme value associated with the index, corresponding
     * to the previous (technically current depending on its placement) token.
     *
     * @return <code>String</code>
     */
    private String getToken() {
        return this.getTokensList().get(this.tokenIndex - 1).getToken();
    }

    // Format progression-checking methods (aka, the "spaghetti code collection")

    /**
     * This method is the first related to the parsing of the file content tokens assembled in the
     * previous lexer methods into a working GUI. This method simply "gets the ball rolling," so to
     * speak, by determining if the ordering of the tokens as represented in the file is in
     * accordance with the grammar's own restrictions. Thus, the testing method in question,
     * <code>RecursiveDescentParser.class.hasWellFormedGUI</code> is called to see if the main
     * part of the GUI is well formed. If it is, and all parts have been properly assembled, the
     * <code>JDialog</code> (originally <code>JFrame</code> until the author learned having two
     * <code>JFrame</code>s is bad form) is displayed and a success message is added to the log.
     *
     * @return void
     */
    private void parseContents() {

        // Declaration
        Application appParent;

        // Cache calls to parent getter
        appParent = this.getParent();

        if (this.hasWellFormedGUI()){
            this.resultsDialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            this.resultsDialog.setLocationRelativeTo(appParent.getMainFrame());
            this.resultsDialog.setVisible(true);
            this.addLogEntry("Success: File '" + appParent.getFileName()
                + "' successfully parsed!");
        } else {
            this.addLogEntry("Error: File '" + appParent.getFileName()
                + "' parsing failed.");
        }
    }

    /**
     * This method is one of several that run through the listing of <code>Token</code>s stored at
     * <code>RecursiveDescentParser.class.tokensList</code> to ensure that tokens follow the grammar
     * conventions established in the project rubric.
     * <br />
     * <br />
     * As stated before, the author is largely dissatisfied with this method. Originally, the method
     * made use of nested <code>if...else</code> statement blocks that took the form of a concave
     * arc. As this was very unreadable, the author moved each individual token <code>Type</code> in
     * the grammar progression to its own <code>if...else</code> block, denoted with an all-caps
     * comment above it corresponding to the next legal token. However, even so, the method, like
     * those that share a similar form below, is still very difficult to peruse casually. The author
     * wishes the project were more of a class-length affair rather than a week's affair, as this
     * would have allowed him to experiment with cleaner approaches that could possibly do away with
     * much of the copy/pasta that litters this methods.
     *
     * @return <code>boolean</code>
     */
    private boolean hasWellFormedGUI() {

        // Declarations
        int resultsWidth, resultsHeight;
        String methodName;

        // Definitions
        methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
        this.currentTokenType = this.getNextTokenType();

        // WINDOW
        if (this.currentTokenType == Type.WINDOW) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.WINDOW, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // STRING - WINDOW TITLE
        if (this.currentTokenType == Type.STRING) {
            this.resultsDialog = new JDialog();
            this.resultsDialog.setTitle(this.getToken());
            this.currentElement = this.resultsDialog;
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.STRING, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // LEFT PARENTHESIS OF DIMENSIONS
        if (this.currentTokenType == Type.LPAREN) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.LPAREN, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // NUMBER - WIDTH OF WINDOW
        if (this.currentTokenType == Type.NUMBER) {
            try {
                resultsWidth = Integer.parseInt(this.getToken());
                this.currentTokenType = this.getNextTokenType();
            } catch (NumberFormatException ex) {
                return this.logErrorMessage("Error: Illegitimate width value of "
                    + this.getToken(), this.getTokenLineNumber(), methodName);
            }
        } else {
            return this.logErrorMessage(Type.NUMBER, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // COMMA B/W DIMENSIONS
        if (this.currentTokenType == Type.COMMA) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.COMMA, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // NUMBER - HEIGHT OF WINDOW
        if (this.currentTokenType == Type.NUMBER) {
            try {
                resultsHeight = Integer.parseInt(this.getToken());
                this.currentTokenType = this.getNextTokenType();
            } catch (NumberFormatException ex) {
                return this.logErrorMessage("Error: Illegitimate height value of "
                    + this.getToken(), this.getTokenLineNumber(), methodName);
            }
        } else {
            return this.logErrorMessage(Type.NUMBER, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // RIGHT PARENTHESIS OF DIMENSIONS
        if (this.currentTokenType == Type.RPAREN) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.RPAREN, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // CHECK LAYOUT
        if (this.hasWellFormedLayout()) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage("Error: Malformed layout detected.",
                this.getTokenLineNumber(), methodName);
        }

        // CHECK FOR ANY WIDGETS
        this.hasAdditionalTokens("hasWellFormedWidget");

        // FILE END
        if (this.currentTokenType == Type.END) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.END, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // PERIOD - FINAL TOKEN
        if (this.currentTokenType == Type.PERIOD) {
            this.resultsDialog.setSize(resultsWidth, resultsHeight);
            return true;
        } else {
            return this.logErrorMessage(Type.PERIOD, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }
    }

    /**
     * As implied by the name, this method simply checks, in a manner similar to other spaghetti
     * code methods above and below, the grammatical composition of the tokens contained within the
     * <code>RecursiveDescentParser.class.tokensList</code> <code>ArrayList</code>. In a properly
     * formatted Layout, the progression should be LAYOUT -> GRID/FLOW (and associated numbers) ->
     * COLON.
     *
     * @return <code>boolean</code>
     */
    private boolean hasWellFormedLayout() {

        // Declaration
        String methodName;

        // Definition
        methodName= Thread.currentThread().getStackTrace()[1].getMethodName();

        // LAYOUT
        if (this.currentTokenType == Type.LAYOUT) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.LAYOUT, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // GRID or FLOW LAYOUT MANGAGER W/ PARENTHESES
        if (this.hasWellFormedLayoutManager()) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage("Error: Expected " + Type.FLOW + " or " + Type.GRID
                + ", encountered " + this.getTokenType(), this.getTokenLineNumber(), methodName);
        }

        // COLON
        if (this.currentTokenType == Type.COLON) {
            return true;
        } else {
            return this.logErrorMessage(Type.COLON, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }
    }

    /**
     * This method is invoked by <code>RecursiveDescentParser.class.hasWellFormedLayout</code> to
     * determine whether or not the <code>GridLayout</code> or <code>FlowLayout</code> managers have
     * been assembled properly, with all optional values accounted for in the case of the former
     * manager. This method was, like those above and below it, a mess of spaghetti code. Originally
     * there was a pair of nested <code>switch</code> blocks which saddened the author greatly.
     * However, these have since been removed with the relevant code moved to a separate method,
     * <code>RecursiveDescentParser.class.hasWellFormedGridLayout</code>.
     *
     * @return <code>boolean</code>
     */
    private boolean hasWellFormedLayoutManager() {
        switch(this.currentTokenType) {
            case FLOW:
                this.currentElement.setLayout(new FlowLayout());
                return true;
            case GRID:
                return this.hasWellFormedGridLayout();
            default:
                return false;
        }
    }

    /**
     * This method was originally a part of the above layout manager checking method, namely
     * <code>RecursiveDescentParser.class.hasWellFormedLayoutManager</code>, but was moved to a
     * separate method when the need for a pair of nested <code>switch</code> blocks arose. As per
     * <a href="https://stackoverflow.com/a/1583748">this answer's</a> precedent, the second block
     * was moved to a separate method with an appropriate name. This also helps improve readability
     * in the above method by making method invocation tracing much easier.
     *
     * @return <code>boolean</code>
     */
    private boolean hasWellFormedGridLayout() {

        // Declarations
        int rows, cols, hgap, vgap;
        String methodName;

        // Definition
        methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        this.currentTokenType = this.getNextTokenType();

        // LEFT PARENTHESIS OF GRID DIMENSIONS
        if (this.currentTokenType == Type.LPAREN) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.LPAREN, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // NUMBER - NUM ROWS
        if (this.currentTokenType == Type.NUMBER) {
            try {
                rows = Integer.parseInt(this.getToken());
                this.currentTokenType = this.getNextTokenType();
            } catch (NumberFormatException ex) {
                return this.logErrorMessage("Error: Illegitimate rows value of " + this.getToken(),
                    this.getTokenLineNumber(), methodName);
            }
        } else {
            return this.logErrorMessage(Type.NUMBER, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // COMMA B/W ROWS/COLS
        if (this.currentTokenType == Type.COMMA) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.COMMA, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // NUMBER - NUM COLUMNS
        if (this.currentTokenType == Type.NUMBER) {
            try {
                cols = Integer.parseInt(this.getToken());
                this.currentTokenType = this.getNextTokenType();
            } catch (NumberFormatException ex) {
                return this.logErrorMessage("Error: Illegitimate columns value of "
                    + this.getToken(), this.getTokenLineNumber(), methodName);
            }
        } else {
            return this.logErrorMessage(Type.NUMBER, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // RIGHT PARENTHESIS OR COMMA OF GRID DIMENSIONS
        switch(this.currentTokenType) {
            case RPAREN:
                this.currentElement.setLayout(new GridLayout(rows, cols));
                return true;
            case COMMA:
                this.currentTokenType = this.getNextTokenType();
                break;
            default:
                return this.logErrorMessage("Error: Expected " + Type.COMMA + " or " + Type.RPAREN
                    + ", encountered " + this.getTokenType(), this.getTokenLineNumber(),
                    methodName);
        }

        // NUMBER - HGAP
        if (this.currentTokenType == Type.NUMBER) {
            try {
                hgap = Integer.parseInt(this.getToken());
                this.currentTokenType = this.getNextTokenType();
            } catch (NumberFormatException ex) {
                return this.logErrorMessage("Error: Illegitimate hgap value of " + this.getToken(),
                    this.getTokenLineNumber(), methodName);
            }
        } else {
            return this.logErrorMessage(Type.NUMBER, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // COMMA B/W GAPS
        if (this.currentTokenType == Type.COMMA) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.COMMA, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // NUMBER - VGAP
        if (this.currentTokenType == Type.NUMBER) {
            try {
                vgap = Integer.parseInt(this.getToken());
                this.currentTokenType = this.getNextTokenType();
            } catch (NumberFormatException ex) {
                return this.logErrorMessage("Error: Illegitimate vgap value of " + this.getToken(),
                    this.getTokenLineNumber(), methodName);
            }
        } else {
            return this.logErrorMessage(Type.NUMBER, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        // RIGHT PARENTHESIS
        if (this.currentTokenType == Type.RPAREN) {
            this.currentElement.setLayout(new GridLayout(rows, cols, hgap, vgap));
            return true;
        } else {
            return this.logErrorMessage(Type.RPAREN, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }
    }

    /**
     * This method was created to consolidate two instances of copy/pasta code previously located in
     * the <code>Type.PANEL</code> and <code>Type.GROUP</code> cases of the above method's own
     * <code>switch</code> statement. It accepts as a parameter the name of a method that will be
     * passed to the reflection method <code>RecursiveDescentParser.class.hasAdditionalTokens</code>
     * method. This method simply ensures that all widgets end with <code>Type.END</code> and
     * <code>Type.SEMICOLON</code> as per the grammar of the project.
     *
     * @param methodName <code>String</code> method name for <code>hasAdditionalTokens()</code>
     * @return <code>boolean</code>
     */
    private boolean isWellFormedWidgetEnding(String method) {

        // Declaration
        String methodName;

        // Definition
        methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        this.hasAdditionalTokens(method);
        if (this.currentTokenType == Type.END) {
            this.currentTokenType = this.getNextTokenType();
        } else {
            return this.logErrorMessage(Type.END, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        if (this.currentTokenType == Type.SEMICOLON) {
            return true;
        } else {
            return this.logErrorMessage(Type.SEMICOLON, this.getTokenType(),
                this.getTokenLineNumber(), methodName);
        }
    }

    // Reflection methods (for recursion purposes)

    /**
     * This method makes use of reflection to reduce copy/pasta that existed in one of the earlier
     * iterations of the program. This method replaces a pair of identical methods named
     * <code>hasRadioButtons()</code> and <code>hasWellFormedWidgets()</code> that the author
     * determined could be consolidated. Though this is likely an expensive operation in comparison
     * to the copy/pasta, it produces a much more readable alternative.
     * <br />
     * <br />
     * As far as the method's purpose goes, it exists to recursively (as per the <em>recursive</em>
     * part of "recursive descent parser") parse any additional widgets or radio buttons that may
     * exist, per the grammar outlined in the project rubric. This method was rather awkward to
     * write, as it felt <span style="color:yellow; font-family:'Comic Sans MS'">jAnKeD</span> to
     * use a temporary field in the form of <code>currentIndex</code> to store the current position
     * in <code>RecursiveDescentParser.class.tokensList</code> while testing methods are invoked to
     * check for further widgets/buttons. The author is sure there is a better way to go about this
     * process, but due to the one week timeframe of construction, further research was unable to be
     * conducted.
     *
     * @see java.lang.reflect
     * @param methodName <tt>String</tt>, hasWellFormedRadioButton or hasWellFormedWidget
     * @return <code>boolean</code>
     */
    private boolean hasAdditionalTokens(String methodName) {

        // Declarations
        int currentIndex;
        boolean hasWellFormedResult;
        Method hasWellFormedElement;

        // Preserve current tokenIndex in case of no further proper widgets
        currentIndex = this.tokenIndex;

        try {
            // Either hasWellFormedRadioButton() or hasWellFormedWidget()
            hasWellFormedElement = RecursiveDescentParser.class.getDeclaredMethod(methodName);
            hasWellFormedResult = (boolean) hasWellFormedElement.invoke(this);

            if (hasWellFormedResult) {
                this.currentTokenType = this.getNextTokenType();
                this.hasAdditionalTokens(methodName);
                return true;
            } else {
                // Revert to preserved index
                this.tokenIndex = currentIndex;
                return false;
            }
        } catch (
            NoSuchMethodException |
            SecurityException |
            IllegalAccessException |
            IllegalArgumentException |
            InvocationTargetException ex
        ) {
            return this.logErrorMessage("Error: " + ex, this.getTokenLineNumber(), methodName);
        }
    }

    /**
     * The other main method making use of the Java reflection technique, this method is used by
     * <code>RecursiveDescentParser.class.hasWellFormedWidget</code> to handle the
     * creation and placement of various <tt>Swing</tt> class objects (like <code>JButton</code>s,
     * <code>JTextField</code>s, <code>JLabel</code>s, <code>JRadioButton</code>s) that may need to
     * be added to the GUI. The method finally checks to ensure the widget ends with the appropriate
     * <code>Type.SEMICOLON</code> as expected.
     *
     * This method resulted in a much more readable widgets method and cleaned up a lot of
     * duplicate code. It is admittedly a bit difficult to divine if the reader is unfamiliar with
     * reflection, but the author has tried to elucidate the nature of each field by naming them as
     * explicitly as possible.
     *
     * @see java.lang.reflect
     * @param className <code>String</code> name of the Swing class to create
     * @param type <code>Type</code> expected within widget grammar
     * @return <code>boolean</code>
     */
    private boolean isWellFormedWidget(String className, Type type) {

        // Declarations
        Constructor constructor;
        Class<?> swingClass, expectedParameterClass;
        Object param;
        Container newSwingClassInstance;
        String methodName;

        // Use for logging purposes
        methodName = Thread.currentThread().getStackTrace()[1].getMethodName();

        // Shoehorned in to allow use of method for JRadioButtons too (last-minute hack)
        if (!className.equals("JRadioButton")) {
            this.currentTokenType = this.getNextTokenType();
        }

        if (this.currentTokenType == type) {
            try {
                // Set parameter likely to appear in Swing class's constructor
                expectedParameterClass = (type == Type.NUMBER) ? Integer.TYPE : String.class;
                param = (type == Type.NUMBER) ? Integer.parseInt(this.getToken()) : this.getToken();

                // Grabs Swing class, gets proper constructor, creates new instance, passes param
                swingClass = Class.forName("javax.swing." + className);
                constructor = swingClass.getConstructor(expectedParameterClass);
                newSwingClassInstance = (Container) constructor.newInstance(param);

                this.currentTokenType = this.getNextTokenType();
            } catch (
                ClassNotFoundException |
                InstantiationException |
                NoSuchMethodException |
                IllegalAccessException |
                NumberFormatException |
                InvocationTargetException ex
            ) {
                return this.logErrorMessage("Error: " + ex, this.getTokenLineNumber(), methodName);
            }
        } else {
            return this.logErrorMessage(type, this.getTokenType(), this.getTokenLineNumber(),
                methodName);
        }

        if (this.currentTokenType == Type.SEMICOLON) {
            this.currentElement.add(newSwingClassInstance);

            // Shoehorned in to allow use of method for JRadioButtons too (last-minute hack)
            if (className.equals("JRadioButton")) {
                this.radioGroup.add((JRadioButton) newSwingClassInstance);
            }

            return true;
        } else {
            return this.logErrorMessage(Type.SEMICOLON, this.getTokenType(),
                this.getTokenLineNumber(), methodName);
        }
    }

    // Reflection helper methods

    /**
     * This method is used to determine which sort of widget is being assembled. Previously, this
     * method was awash in copy/pasta as a tangled mass of <code>if...else</code> statement blocks.
     * However, the application of reflection to the problem at hand, as well as the consolidation
     * of identical blocks of code with slight differences, allowed the author to clean up the mess
     * and move the necessary operations into specific methods of their own, improving readability.
     *
     * @return <code>boolean</code>
     */
    private boolean hasWellFormedWidget() {

        // Declarations
        Container parentElement;
        JPanel newPanel;

        switch (this.currentTokenType) {
            case PANEL:
                this.currentTokenType = this.getNextTokenType();

                // Definitions
                parentElement = this.currentElement;
                newPanel = new JPanel();
                parentElement.add(newPanel);
                this.currentElement = newPanel;

                if (this.hasWellFormedLayout()) {
                    this.currentTokenType = this.getNextTokenType();
                }

                if (this.isWellFormedWidgetEnding("hasWellFormedWidget")) {
                    this.currentElement = parentElement;
                    return true;
                } else {
                    return false;
                }
            case GROUP:
                this.currentTokenType = this.getNextTokenType();
                this.radioGroup = new ButtonGroup();

                return this.isWellFormedWidgetEnding("hasWellFormedRadioButton");
            case BUTTON:
                return this.isWellFormedWidget("JButton", Type.STRING);
            case TEXTFIELD:
                return this.isWellFormedWidget("JTextField", Type.NUMBER);
            case LABEL:
                return this.isWellFormedWidget("JLabel", Type.STRING);
            case END:
                return false;
            default: // Catchall WIDGET term used here to simplify logged error message
                return this.logErrorMessage(Type.WIDGET, this.getTokenType(),
                    this.getTokenLineNumber(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
        }
    }

    /**
     * This method is used by the parser's recursive reflection method to check that A) radio button
     * widgets are properly formatted in accordance with the project rubric grammar specifications
     * and B) that additional <code>Type.RADIO</code> tokens exist, necessitating further recursion.
     *
     * @return <code>boolean</code>
     */
    private boolean hasWellFormedRadioButton() {
        switch(this.currentTokenType) {
            case RADIO:
                this.currentTokenType = this.getNextTokenType();
                return this.isWellFormedWidget("JRadioButton", Type.STRING);
            case END:
                return false;
            default:
                return this.logErrorMessage(Type.RADIO, this.getTokenType(),
                    this.getTokenLineNumber(),
                    Thread.currentThread().getStackTrace()[1].getMethodName());
        }
    }
}