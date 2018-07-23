/**
 * Token.java - Class for new tokens created from file contents
 * Begun 07/08/18
 * @author Andrew Eissen
 */
//package recursivedescentparser;

/**
 * This simple class is used for building proper tokens encountered within the body of the main
 * <code>RecursiveDescentParser.class</code> instance. Each particular token encountered is provided
 * with the most appropriate <code>Type</code> from the <code>enum</code> class of the same name and
 * used to create a new <code>Token</code> object. These objects are then stored in the parser's
 * <code>RecursiveDescentParser.class.tokensList</code> <code>ArrayList</code> and used to assemble
 * the GUI. To assist the user in uncovering file errors, the line number on which the token is
 * found in the file is included as a field that can be used to log messages in the status GUI log.
 * <br />
 * <br />
 * @author Andrew Eissen
 */
final class Token {

    // Declarations
    private Type type;
    private String token;
    private int lineNumber;

    /**
     * Default constructor
     */
    protected Token() {
        this.setType(Type.UNKNOWN);
        this.setToken("");
        this.setLineNumber(0);
    }

    /**
     * Parameterized constructor
     * @param type <code>Type</code>
     * @param token <code>String</code>
     * @param lineNumber <code>int</code>
     */
    protected Token(Type type, String token, int lineNumber) {
        this.setType(type);
        this.setToken(token);
        this.setLineNumber(lineNumber);
    }

    // Setters

    /**
     * Setter for <code>type</code>
     * @param type <code>Type</code>
     * @return void
     */
    private void setType(Type type) {
        this.type = type;
    }

    /**
     * Setter for <code>token</code>
     * @param token <code>String</code>
     * @return void
     */
    private void setToken(String token) {
        this.token = token;
    }

    /**
     * Setter for <code>lineNumber</code>
     * @param lineNumber <code>int</code>
     * @return void
     */
    private void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    // Getters

    /**
     * Getter for <code>type</code>
     * @return type
     */
    protected Type getType() {
        return this.type;
    }

    /**
     * Getter for <code>token</code>
     * @return token
     */
    protected String getToken() {
        return this.token;
    }

    /**
     * Getter for <code>lineNumber</code>
     * @return lineNumber
     */
    protected int getLineNumber() {
        return this.lineNumber;
    }
}