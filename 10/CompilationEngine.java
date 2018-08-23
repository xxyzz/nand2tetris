/******************************************************************************
 *  Compilation:  javac-algs4 CompilationEngine.java
 *  Execution:    java-algs4 CompilationEngine
 *  Dependencies: JackTokenizer.java
 * 
 *  Generates the compiler's output, recursive top-down parser.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Out;

public class CompilationEngine {
    private final JackTokenizer jackTokenizer;
    private final Out out;

    // Creates a new compilation engine with the given input and output. The next routine called must be compileClass().
    public CompilationEngine(JackTokenizer jackTokenizer, Out output) {
        if (jackTokenizer == null || output == null) throw new IllegalArgumentException("Argument is null");
        this.jackTokenizer = jackTokenizer;
        out = output;
    }

    // Compiles a complete class.
    public void compileClass() {
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.CLASS) {
            out.println("<class>\n<keyword> class </keyword>");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
                out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                jackTokenizer.advance();
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '{') {
                    out.println("<symbol> { </symbol>");
                    jackTokenizer.advance();
                    compileClassVarDec();
                    jackTokenizer.advance();
                    compileSubroutineDec();
                }
            }
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '}') {
                out.println("<symbol> } </symbol>\n</class>");
            }
        }
    }

    // Compiles a static declaration or a field declaration.
    public void compileClassVarDec() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && (jackTokenizer.keyWord() == JackTokenizer.STATIC || jackTokenizer.keyWord() == JackTokenizer.FIELD)) {
            out.println("<classVarDec>");
            if (jackTokenizer.keyWord() == JackTokenizer.STATIC) {
                out.println("<keyword> static </keyword>");
                jackTokenizer.advance();
                compileType();
                jackTokenizer.advance();
                compileVarName();
            }
            else if (jackTokenizer.keyWord() == JackTokenizer.FIELD) {
                out.println("<keyword> field </keyword>");
                jackTokenizer.advance();
                compileType();
                jackTokenizer.advance();
                compileVarName();
            }
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';')
                out.println("<symbol> ; </symbol>");
            out.println("</classVarDec>");

            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && (jackTokenizer.keyWord() == JackTokenizer.STATIC || jackTokenizer.keyWord() == JackTokenizer.FIELD))
                compileClassVarDec();
        }
        else compileSubroutineDec();
    }

    private boolean isType(int token) {
        switch (token) {
            case JackTokenizer.INT:
                return true;
            case JackTokenizer.CHAR:
                return true;
            case JackTokenizer.BOOLEAN:
                return true;
            default:
                return false;
        }
    }

    // Compiles int, char, boolean or className
    private void compileType() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD) {
            switch (jackTokenizer.keyWord()) {
                case JackTokenizer.INT:
                    out.println("<keyword> int </keyword>");
                    break;
                case JackTokenizer.CHAR:
                    out.println("<keyword> char </keyword>");
                    break;
                case JackTokenizer.BOOLEAN:
                    out.println("<keyword> boolean </keyword>");
                    break;
                default:
                    break;
            }
        }
        else if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
        }
    }

    private void compileVarName() {
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
        }
        jackTokenizer.advance();
        while (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ',') {
            out.println("<symbol> , </symbol>");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
                out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            }
            jackTokenizer.advance();
        }
    }

    // Compiles a complete method, function, or constructor.
    public void compileSubroutineDec() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD) {
            int keyWord = jackTokenizer.keyWord();
            if (keyWord == JackTokenizer.CONSTRUCTOR) {
                out.println("<subroutineDec>\n<keyword> construction </keyword>");
                jackTokenizer.advance();
                privateCompileSubroutine();
            }
            else if (keyWord == JackTokenizer.FUNCTION) {
                out.println("<subroutineDec>\n<keyword> function </keyword>");
                jackTokenizer.advance();
                privateCompileSubroutine();
            }
            else if (keyWord == JackTokenizer.METHOD) {
                out.println("<subroutineDec>\n<keyword> method </keyword>");
                jackTokenizer.advance();
                privateCompileSubroutine();
            }
            
        }
    }
    
    private void privateCompileSubroutine() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD) {
            int keyWord = jackTokenizer.keyWord();
            if (keyWord == JackTokenizer.VOID) {
                out.println("<keyword> void </keyword>");
                jackTokenizer.advance();
                anotherPrivateSubroutine();
            }
            else if (isType(keyWord)) {
                compileType();
                jackTokenizer.advance();
                anotherPrivateSubroutine();
            }   
        }
    }

    private void anotherPrivateSubroutine() {
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            // subroutineName
            out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
                out.println("<symbol> ( </symbol>");
                out.println("<parameterList>");
                jackTokenizer.advance();
                // empty parameter list
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                    out.println("</parameterList>");
                    out.println("<symbol> ) </symbol>");
                    out.println("<subroutineBody>");
                    jackTokenizer.advance();
                    compileSubroutineBody();
                    out.println("</subroutineBody>");
                }
                else {
                    compileParameterList();
                    out.println("</parameterList>");
                    jackTokenizer.advance();
                    if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                        out.println("<symbol> ) </symbol>");
                        out.println("<subroutineBody>");
                        jackTokenizer.advance();
                        compileSubroutineBody();
                        out.println("</subroutineBody>");
                    }
                }
            }
            out.println("</subroutineDec>");
        }  
    }

    // Compiles a (possibly empty) parameter list. Does not handle the enclosing ‘‘()’’.
    public void compileParameterList() {
        compileType();
        jackTokenizer.advance();
        compileVarName();
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ',') {
            jackTokenizer.advance();
            compileParameterList();
        }           
    }

    // Complies a subroutine's body
    public void compileSubroutineBody() {
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '{') {
            out.println("<symbol> { </symbol>");
            jackTokenizer.advance();
            compileVarDec();
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '}')
                out.println("<symbol> } </symbol>");
        }
    }

    // Compiles a var declaration.
    public void compileVarDec() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.VAR) {
            out.println("<varDec>\n<keyword> var </keyword>");
            jackTokenizer.advance();
            compileType();
            jackTokenizer.advance();
            compileVarName();

            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';')
                out.println("<symbol> ; </symbol>\n</varDec>");
            jackTokenizer.advance();

            if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.VAR)
                compileVarDec();
            else compileStatements();
        }
    }

    // Compiles a sequence of statements. Does not handle the enclosing ‘‘{}’’.
    public void compileStatements() {

    }

    // Compiles a let statement.
    public void compileLet() {

    }

    // Compiles an if statement, possibly with a trailing else clause.
    public void compileIf() {

    }

    // Compiles a while statement.
    public void compileWhile() {

    }

    // Compiles a do statement.
    public void compileDo() {

    }

    // Compiles a return statement.
    public void compileReturn() {

    }

    // Compiles an expression.
    public void compileExpression() {

    }

    /**
     * Compiles a term. If the current token is an identifier, the routine must distinguish between a variable,
     * an array entry, and a subroutine call. A single lookahead token, which may be one of ‘‘[’’, ‘‘(’’, or
     * ‘‘.’’ suffices to distinguish between the three possibilities. Any other token is not part of this
     * term and should not be advanced over.
     */
    public void compileTerm() {

    }

    // Compiles a ( possibly empty) comma-separated list of expressions.
    public void compileExpressionList() {

    }
}