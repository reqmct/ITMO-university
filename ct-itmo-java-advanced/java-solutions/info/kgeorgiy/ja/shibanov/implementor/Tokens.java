package info.kgeorgiy.ja.shibanov.implementor;

/**
 * Contains constants used in the generating classes.
 */
public class Tokens {
    /**
     * Default constructor.
     */
    public Tokens() {
    }

    /**
     * The line separator of the system.
     */

    public final static String LINE_SEPARATOR = System.lineSeparator();

    /**
     * The empty string.
     */
    public final static String EMPTY = "";
    /**
     * The single whitespace character.
     */
    public final static String SPACE = " ";
    /**
     * The right curly brace character.
     */
    public final static String RIGHT_CURLY_BRACE = "}";
    /**
     * The parameter separator (comma with whitespace).
     */
    public final static String PARAMETER_SEPARATOR = ", ";
    /**
     * The tabulation string (four whitespaces).
     */
    public final static String TABULATION = SPACE.repeat(4);
    /**
     * The literal string representing {@code 0}.
     */
    public final static String ZERO = "0";
    /**
     * The literal string representing {@code false}.
     */
    public final static String FALSE = "false";
    /**
     * The literal string representing {@code null}.
     */
    public final static String NULL = "null";
    /**
     * The suffix used for generated classes.
     */
    public final static String IMPLEMENTATION = "Impl";
}
