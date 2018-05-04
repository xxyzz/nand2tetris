/******************************************************************************
 *  Compilation:  javac-algs4 Parser.java
 *  Execution:    java-algs4 Parser
 *  Dependencies:
 * 
 *  Encapsulates access to the input code. Reads an assembly language command,
 *  parses it, and provides convenient access to the command’s components(fields and symbols).
 *  In addition, removes all white space and comments.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
// import edu.princeton.cs.algs4.StdOut;

public class Parser {
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
            String readCommand = in.readLine().replaceAll("\\s+", "");
            if (!readCommand.equals("") && readCommand.charAt(0) != '/') {
                if (readCommand.contains("/")) readCommand = readCommand.substring(0, readCommand.indexOf('/'));
                command = readCommand;
            }
            else advance();
        }
    }

    /**
     * Returns the type of the current command:
     * A_COMMAND for @Xxx where Xxx is either a symbol or a decimal number
     * C_COMMAND for dest=comp;jump
     * L_COMMAND (actually, pseudo-command) for (Xxx) where Xxx is a symbol.
     */
    public int commandType() {
        // A_COMMAND
        if (command.charAt(0) == '@') return 0;
        // L_COMMAND
        else if (command.charAt(0) == '(') return 2;
        // C_COMMAND
        return 1;
    }

    /**
     * Returns the symbol or decimal Xxx of the current command @Xxx or (Xxx).
     * Should be called only when commandType() is A_COMMAND or L_COMMAND.
     */
    public String symbol() {
        if (commandType() == 0) {
            return command.substring(1);
        }
        if (commandType() == 2) {
            return command.substring(1, command.lastIndexOf(')'));
        }
        return "";
    }
    
    /**
     * Returns the dest mnemonic in the current C-command (8 possi-bilities).
     * Should be called only when commandType() is C_COMMAND .
     */
    public String dest() {
        if (commandType() == 1) {
            if (command.contains("=")) return command.substring(0, command.indexOf('='));
        }
        return "";
    }

    /**
     * Returns the comp mnemonic in the current C-command (28 pos-sibilities).
     * Should be called only when commandType() is C_COMMAND .
     */
    public String comp() {
        if (commandType() == 1) {
            if (command.contains("=")) {
                if (command.contains(";")) return command.substring(command.indexOf('=') + 1, command.indexOf(';'));
                else return command.substring(command.indexOf('=') + 1);
            }
            else if (command.contains(";")) return command.substring(0, command.indexOf(';'));
        }
        return "";
    }

    /**
     * Returns the jump mnemonic in the current C-command (8 pos-sibilities).
     * Should be called only when commandType() is C_COMMAND .
     */
    public String jump() {
        if (commandType() == 1) {
            if (command.contains(";")) return command.substring(command.indexOf(';') + 1);
        }
        return "";
    }
}