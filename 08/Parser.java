/******************************************************************************
 *  Compilation:  javac-algs4 Parser.java
 *  Execution:
 *  Dependencies:
 * 
 *  Parses each VM command into its lexical elements
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Parser {
    // command type constants declaration
    public static final int C_ARITHMETIC = 0, C_PUSH = 1, C_POP = 2, C_LABEL = 3, C_GOTO = 4, 
        C_IF = 5, C_FUNCTION = 6, C_RETURN = 7, C_CALL = 8;

    private final In in;
    private String command;

    // Opens the input file/stream and gets ready to parse it.
    public Parser(In in) {
        this.in = in;
    }

    // Are there more commands in the input?
    public boolean hasMoreCommands() {
        return in.hasNextLine();
    }

    /**
     * Reads the next command from the input and makes it the current command.
     * Should be called only if hasMoreCommands() is true.
     * Initially there is no current command.
     */
    public void advance() {
        if (hasMoreCommands()) {
            // read one line
            String readCommand = in.readLine();
            // skip comments
            if (!readCommand.equals("") && readCommand.charAt(0) != '/') {
                // remove the comments after commands
                if (readCommand.contains("/")) readCommand = readCommand.substring(0, readCommand.indexOf('/')).trim();
                command = readCommand;
            }
            // try next line
            else advance();
        }
    }

    /**
     * Returns a constant representing the type of the current command
     * C_ARITHMETIC is returned for all the arithmetic/logical commands
     * C_PUSH, C_POP, C_LABEL, C_GOTO, C_IF, C_FUNCTION, C_RETURN, C_CALL
     */
    public int commandType() {
        if (command.length() > 4 && command.substring(0, 4).equals("push")) return C_PUSH;
        else if (command.length() > 3 && command.substring(0, 3).equals("pop")) return C_POP;
        else if (command.length() > 5 && command.substring(0, 5).equals("label")) return C_LABEL;
        else if (command.length() > 4 && command.substring(0, 4).equals("goto")) return C_GOTO;
        else if (command.length() > 2 && command.substring(0, 2).equals("if")) return C_IF;
        else if (command.length() > 8 && command.substring(0, 8).equals("function")) return C_FUNCTION;
        else if (command.equals("return")) return C_RETURN;
        else if (command.length() > 4 && command.substring(0, 4).equals("call")) return C_CALL;
        else return C_ARITHMETIC;
    }

    /**
     * Returns the first argument of the current command.
     * In the case of C_ARITHMETIC, the command itself(add, sub, etc.) is returned.
     * Should not be called if the current command is C_RETURN.
     */
    public String arg1() {
        int commandType = commandType();
        if (commandType != C_RETURN) {
            if (commandType == C_ARITHMETIC) return command;
            else return command.split(" ")[1];
        }
        return null;
    }

    /**
     * Returns the second argument of the current command.
     * Should be called only if the current command is C_PUSH, C_POP, C_FUNCTION, or C_CALL.
     */
    public int arg2() {
        int commandType = commandType();
        if (commandType == C_PUSH || commandType == C_POP || commandType == C_FUNCTION || commandType == C_CALL) {
            return Integer.parseInt(command.split(" ")[2]);
        }
        return 0;
    }

    // test
    public static void main(String[] args) {
        In in = new In(args[0]);
        while (!in.isEmpty()) {
            Parser parser = new Parser(in);
            parser.advance();
            StdOut.println("arg1: " + parser.arg1());
            StdOut.println("arg2: " + parser.arg2());
        }
    }
}