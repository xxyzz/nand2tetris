/******************************************************************************
 *  Compilation:  javac-algs4 JackTokenizer.java
 *  Execution:    java-algs4 JackTokenizer Square/SquareGame.jack
 *  Dependencies:
 * 
 *  Removes all comments and white space from the input stream and breaks it 
 *  into Jack-language tokens.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;

public class JackTokenizer {
    // types of token
    public static final int KEYWORD = 0, SYMBOL = 1, IDENTIFIER = 2, INT_CONST = 3,
        STRING_CONST = 4;

    // keywords
    public static final int CLASS = 0, METHOD = 1, FUNCTION = 2, CONSTRUCTOR = 3, INT = 4,
        BOOLEAN = 5, CHAR = 6, VOID = 7, VAR = 8, STATIC = 9, FIELD = 10, LET = 11, DO = 12,
        IF = 13, ELSE = 14, WHILE = 15, RETURN = 16, TRUE = 17, FALSE = 18, NULL = 19, THIS = 20;

    private static SET<String> keywords = new SET<String>();
    private static SET<String> symbols = new SET<String>();

    
    static {
        keywords.add("class");
        keywords.add("constructor");
        keywords.add("function");
        keywords.add("method");
        keywords.add("field");
        keywords.add("static");
        keywords.add("var");
        keywords.add("int");
        keywords.add("char");
        keywords.add("boolean");
        keywords.add("void");
        keywords.add("true");
        keywords.add("false");
        keywords.add("null");
        keywords.add("this");
        keywords.add("let");
        keywords.add("do");
        keywords.add("if");
        keywords.add("else");
        keywords.add("while");
        keywords.add("return");
        
        symbols.add("{");
        symbols.add("}");
        symbols.add("(");
        symbols.add(")");
        symbols.add("[");
        symbols.add("]");
        symbols.add(".");
        symbols.add(",");
        symbols.add(";");
        symbols.add("+");
        symbols.add("-");
        symbols.add("*");
        symbols.add("/");
        symbols.add("&");
        symbols.add("|");
        symbols.add("<");
        symbols.add(">");
        symbols.add("=");
        symbols.add("~");
    }
    
    private final In in;
    private Queue<String> tokens;
    private String currentToken;
    
    // Opens the input .jack file and gets ready to tokenize it.
    public JackTokenizer(In in) {
        if (in == null) throw new IllegalArgumentException();
        this.in = in;
        tokens = new Queue<String>();
    }
    
    // Are there more tokens in the input?
    public boolean hasMoreTokens() {
        if (!tokens.isEmpty()) return true;
        else {
            advance();
            return !tokens.isEmpty();
        }
    }

    // Gets the next token from the input and makes it the current token.
    // This method should only be called if hasMoreTokens() is true.
    // Initially there is no current token.
    public void advance() {
        if (in.hasNextLine()) {
            // read one line
            String readCommand = in.readLine().trim();
            
            // skip comments
            if (!readCommand.equals("") && readCommand.charAt(0) != '/' && readCommand.charAt(0) != '*') {
                StringBuilder token = new StringBuilder();
                boolean firstQuote = true;
                // remove the comments after commands
                // if (readCommand.matches(".*\\/\\/.*"))
                if (readCommand.contains("//")) readCommand = readCommand.split("\\/\\/")[0].trim();
                for (int i = 0; i < readCommand.length(); i++) {
                    char thisChar = readCommand.charAt(i);
                    if (symbols.contains(String.valueOf(thisChar)) || (firstQuote && thisChar == ' ')) {
                        if (token.length() > 0) tokens.enqueue(token.toString());
                        if (thisChar != ' ') tokens.enqueue(String.valueOf(thisChar));
                        token.setLength(0); // clear StringBuilder
                    }
                    // string
                    else if (thisChar == '"') {
                        if (!firstQuote) {
                            token.append(thisChar);
                            tokens.enqueue(token.toString());
                            token.setLength(0);
                        }
                        else token.append(thisChar);
                        firstQuote = !firstQuote;
                    }
                    else {
                        token.append(thisChar);
                    }
                }
            }
            // try next line
            else advance();
        }
    }

    // Returns the type of the current token as a constant.
    public int tokenType() {
        if (tokens.isEmpty()) advance();
        currentToken = tokens.dequeue();
        return tokenType(currentToken);
    }
    
    private int tokenType(String token) {
        if (keywords.contains(token)) return KEYWORD;
        if (symbols.contains(token)) return SYMBOL;
        if (token.matches("\\d+")) return INT_CONST;
        if (token.matches("^\"[^\"\n]*\"$")) return STRING_CONST;
        if (token.matches("^[A-Za-z_]\\w*")) return IDENTIFIER;
        
        return -1;
    }

    // Returns the keyword which is the current token, as a constant.
    // This method should be called only if tokenType is KEYWORD.
    public int keyWord() {
        if (tokenType(currentToken) == KEYWORD) {
            if (currentToken.equals("class")) return CLASS;
            else if (currentToken.equals("method")) return METHOD;
            else if (currentToken.equals("function")) return FUNCTION;
            else if (currentToken.equals("constructor")) return CONSTRUCTOR;
            else if (currentToken.equals("int")) return INT;
            else if (currentToken.equals("boolean")) return BOOLEAN;
            else if (currentToken.equals("char")) return CHAR;
            else if (currentToken.equals("void")) return VOID;
            else if (currentToken.equals("var")) return VAR;
            else if (currentToken.equals("static")) return STATIC;
            else if (currentToken.equals("field")) return FIELD;
            else if (currentToken.equals("let")) return LET;
            else if (currentToken.equals("do")) return DO;
            else if (currentToken.equals("if")) return IF;
            else if (currentToken.equals("else")) return ELSE;
            else if (currentToken.equals("while")) return WHILE;
            else if (currentToken.equals("return")) return RETURN;
            else if (currentToken.equals("true")) return TRUE;
            else if (currentToken.equals("false")) return FALSE;
            else if (currentToken.equals("null")) return NULL;
            else if (currentToken.equals("this")) return THIS;
        }
        return -1;
    }

    // Returns the character which is the current token. Should be called only
    // if tokenType() is SYMBOL.
    public char symbol() {
        if (tokenType(currentToken) == SYMBOL) return currentToken.charAt(0);
        return ' ';
    }

    // Returns the identifier which is the current token. Should be called only
    // if tokenType() is IDENTIFIER.
    public String identifier() {
        if (tokenType(currentToken) == IDENTIFIER) return currentToken;
        return "";
    }

    // Returns the integer value of the current token. Should be called only
    // if tokenType() is INT_CONST.
    public int intVal() {
        if (tokenType(currentToken) == INT_CONST) return Integer.parseInt(currentToken);
        return -1;
    }

    // Returns the string value of the current token, without the two enclosing double quotes.
    // Should be called only if tokenType() is STRING_CONST.
    public String stringVal() {
        if (tokenType(currentToken) == STRING_CONST) return currentToken.substring(1, currentToken.length() - 1);
        return "";
    }

    public static void main(String[] args) {
        In in = new In(args[0]);
        while (!in.isEmpty()) {
            JackTokenizer jackTokenizer = new JackTokenizer(in);
            while (jackTokenizer.hasMoreTokens()) {
                int tokenType = jackTokenizer.tokenType();
                if (tokenType == KEYWORD) StdOut.println("keywords: " + jackTokenizer.keyWord());
                if (tokenType == SYMBOL) StdOut.println("Symbol: " + jackTokenizer.symbol());
                if (tokenType == IDENTIFIER) StdOut.println("Identifier: " + jackTokenizer.identifier());
                if (tokenType == INT_CONST) StdOut.println("Int: " + jackTokenizer.intVal());
                if (tokenType == STRING_CONST) StdOut.println("String: " + jackTokenizer.stringVal());
            }
        }
    }
}