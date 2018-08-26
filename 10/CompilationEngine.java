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
                compileVarName(false);
            }
            else if (jackTokenizer.keyWord() == JackTokenizer.FIELD) {
                out.println("<keyword> field </keyword>");
                jackTokenizer.advance();
                compileType();
                jackTokenizer.advance();
                compileVarName(false);
            }
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';')
                out.println("<symbol> ; </symbol>");
            out.println("</classVarDec>");

            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && (jackTokenizer.keyWord() == JackTokenizer.STATIC || jackTokenizer.keyWord() == JackTokenizer.FIELD))
                compileClassVarDec();
            else compileSubroutineDec();
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
            case JackTokenizer.IDENTIFIER:
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

    private void compileVarName(boolean parameterList) {
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
        }
        jackTokenizer.advance();
        if (!parameterList && jackTokenizer.symbol() == ',') {
            out.println("<symbol> , </symbol>");
            jackTokenizer.advance();
            compileVarName(false);
        } else if (parameterList && jackTokenizer.symbol() == ',') {
            out.println("<symbol> , </symbol>");
            jackTokenizer.advance();
            compileType();
            jackTokenizer.advance();
            compileVarName(true);
        }
    }

    // Compiles a complete method, function, or constructor.
    public void compileSubroutineDec() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD) {
            int keyWord = jackTokenizer.keyWord();
            if (keyWord == JackTokenizer.CONSTRUCTOR) {
                out.println("<subroutineDec>\n<keyword> constructor </keyword>");
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
    
    // 'void' | type
    private void privateCompileSubroutine() {
        if (jackTokenizer.keyWord() == JackTokenizer.VOID) {
            out.println("<keyword> void </keyword>");
            jackTokenizer.advance();
            anotherPrivateSubroutine();
        }
        else if (isType(jackTokenizer.tokenType())) {
            compileType();
            jackTokenizer.advance();
            anotherPrivateSubroutine();
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
            if (jackTokenizer.symbol() == '}') {
                jackTokenizer.advance();
                compileSubroutineDec();
            }
        }
    }

    // Compiles a (possibly empty) parameter list. Does not handle the enclosing ‘‘()’’.
    public void compileParameterList() {
        compileType();
        jackTokenizer.advance();
        compileVarName(true);
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
        }
    }

    // Compiles a var declaration.
    public void compileVarDec() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.VAR) {
            out.println("<varDec>\n<keyword> var </keyword>");
            jackTokenizer.advance();
            compileType();
            jackTokenizer.advance();
            compileVarName(false);

            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
                out.println("<symbol> ; </symbol>\n</varDec>");
                jackTokenizer.advance();
                compileVarDec();
            }
        } else {
            out.println("<statements>");
            compileStatements();
        }
    }

    // Compiles a sequence of statements. Does not handle the enclosing ‘‘{}’’.
    public void compileStatements() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.LET) {
            compileLet();
        }
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.IF) {
            compileIf();
        }
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.WHILE) {
            compileWhile();
        }
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.DO) {
            compileDo();
        }
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.RETURN) {
            compileReturn();
        }
    }

    // Compiles a let statement.
    public void compileLet() {
        out.println("<letStatement>\n<keyword> let </keyword>");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '[') {
                out.println("<symbol> [ </symbol>");
                jackTokenizer.advance();
                out.println("<expression>");
                compileExpression();
                out.println("</expression>");
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ']') {
                    out.println("<symbol> ] </symbol>");
                    jackTokenizer.advance();
                    endCompileLet();
                }
            } else endCompileLet();
        }
    }

    private void endCompileLet() {
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '=') {
            out.println("<symbol> = </symbol>");
            jackTokenizer.advance();
            out.println("<expression>");
            compileExpression();
            out.println("</expression>");
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
                out.println("<symbol> ; </symbol>\n</letStatement>");
            }
            jackTokenizer.advance();
            endSubroutineBody();
        }
    }

    private void endSubroutineBody() {
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '}') {
            out.println("</statements>\n<symbol> } </symbol>");
        } else if (jackTokenizer.keyWord() != JackTokenizer.ELSE) compileStatements();
    }

    // Compiles an if statement, possibly with a trailing else clause.
    public void compileIf() {
        out.println("<ifStatement>\n<keyword> if </keyword>");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
            out.println("<symbol> ( </symbol>");
            jackTokenizer.advance();
            out.println("<expression>");
            compileExpression();
            out.println("</expression>");
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                out.println("<symbol> ) </symbol>");
                jackTokenizer.advance();
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '{') {
                    out.println("<symbol> { </symbol>");
                    jackTokenizer.advance();
                    out.println("<statements>");
                    compileStatements();
                    jackTokenizer.advance();
                    if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.ELSE) {
                        out.println("<keyword> else </keyword>");
                        jackTokenizer.advance();
                        if (jackTokenizer.symbol() == '{') {
                            out.println("<symbol> { </symbol>");
                            out.println("<statements>");
                            jackTokenizer.advance();
                            compileStatements();
                            out.println("</ifStatement>");
                            jackTokenizer.advance();
                            compileStatements();
                        }
                    } else {
                        out.println("</ifStatement>");
                        endSubroutineBody();
                    }
                }
            }
        }
    }

    // Compiles a while statement.
    public void compileWhile() {
        out.println("<whileStatement>\n<keyword> while </keyword>");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
            out.println("<symbol> ( </symbol>");
            jackTokenizer.advance();
            out.println("<expression>");
            compileExpression();
            out.println("</expression>");
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                out.println("<symbol> ) </symbol>");
                jackTokenizer.advance();
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '{') {
                    out.println("<symbol> { </symbol>");
                    jackTokenizer.advance();
                    out.println("<statements>");
                    compileStatements();
                    out.println("</whileStatement>");
                    jackTokenizer.advance();
                    endSubroutineBody();
                }
            }
        }
    }

    // Compiles a do statement.
    public void compileDo() {
        out.println("<doStatement>\n<keyword> do </keyword>");
        jackTokenizer.advance();
        // subroutineCall: subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')'
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL) {
                if (jackTokenizer.symbol() == '(') {
                    out.println("<symbol> ( </symbol>");
                    jackTokenizer.advance();
                    out.println("<expressionList>");
                    compileExpressionList();
                    out.println("</expressionList>");
                    if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                        out.println("<symbol> ) </symbol>");
                        jackTokenizer.advance();
                        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
                            out.println("<symbol> ; </symbol>\n</doStatement>");
                            jackTokenizer.advance();
                            endSubroutineBody();
                        }
                    }
                } else if (jackTokenizer.symbol() == '.') {
                    out.println("<symbol> . </symbol>");
                    jackTokenizer.advance();
                    if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
                        out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                        jackTokenizer.advance();
                        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
                            out.println("<symbol> ( </symbol>");
                            jackTokenizer.advance();
                            out.println("<expressionList>");
                            compileExpressionList();
                            out.println("</expressionList>");
                            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                                out.println("<symbol> ) </symbol>");
                                jackTokenizer.advance();
                                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
                                    out.println("<symbol> ; </symbol>\n</doStatement>");
                                    jackTokenizer.advance();
                                    endSubroutineBody();
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Compiles a return statement.
    public void compileReturn() {
        out.println("<returnStatement>\n<keyword> return </keyword>");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
            out.println("<symbol> ; </symbol>\n</returnStatement>");
            jackTokenizer.advance();
            endSubroutineBody();
        } else {
            out.println("<expression>");
            compileExpression();
            out.println("</expression>");
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
                out.println("<symbol> ; </symbol>\n</returnStatement>");
                jackTokenizer.advance();
                endSubroutineBody();
            }
        }
    }

    // Compiles an expression.
    public void compileExpression() {
        compileTerm();
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL) {
            switch (jackTokenizer.symbol()) {
                case '+':
                    out.println("<symbol> + </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                case '-':
                    out.println("<symbol> - </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                case '*':
                    out.println("<symbol> * </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                case '/':
                    out.println("<symbol> / </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                case '&':
                    out.println("<symbol> &amp; </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                case '|':
                    out.println("<symbol> | </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                case '<':
                    out.println("<symbol> &lt; </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                case '>':
                    out.println("<symbol> &gt; </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                case '=':
                    out.println("<symbol> = </symbol>");
                    jackTokenizer.advance();
                    compileTerm();
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Compiles a term. If the current token is an identifier, the routine must distinguish between a variable,
     * an array entry, and a subroutine call. A single lookahead token, which may be one of '[', '(', or
     * '.' suffices to distinguish between the three possibilities. Any other token is not part of this
     * term and should not be advanced over.
     */
    public void compileTerm() {
        if (jackTokenizer.tokenType() == JackTokenizer.INT_CONST) {
            out.println("<term>\n<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>\n</term>");
            jackTokenizer.advance(); 
        } else if (jackTokenizer.tokenType() == JackTokenizer.STRING_CONST) {
            out.println("<term>\n<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>\n</term>");
            jackTokenizer.advance();
        } else if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD) {
            switch (jackTokenizer.keyWord()) {
                case JackTokenizer.TRUE:
                    out.println("<term>\n<keyword> true </keyword>\n</term>");
                    jackTokenizer.advance();
                    break;
                case JackTokenizer.FALSE:
                    out.println("<term>\n<keyword> false </keyword>\n</term>");
                    jackTokenizer.advance();
                    break;
                case JackTokenizer.NULL:
                    out.println("<term>\n<keyword> null </keyword>\n</term>");
                    jackTokenizer.advance();
                    break;
                case JackTokenizer.THIS:
                    out.println("<term>\n<keyword> this </keyword>\n</term>");
                    jackTokenizer.advance();
                    break;
                default:
                    break;
            }
        } else if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            out.println("<term>");
            // varName
            out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            jackTokenizer.advance();
            // array varName '[' expression ']'
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '[') {
                jackTokenizer.advance();
                out.println("<symbol> [ </symbol>");
                out.println("<expression>");
                compileExpression();
                out.println("</expression>");
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ']') {
                    out.println("<symbol> ] </symbol>\n</term>");
                    jackTokenizer.advance();
                }
            }
            // subroutineCall: (className | varName) '.' subroutineName '(' expressionList ')'
            else if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '.') {
                out.println("<symbol> . </symbol>");
                jackTokenizer.advance();
                if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
                    out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                    jackTokenizer.advance();
                    if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
                        out.println("<symbol> ( </symbol>");
                        jackTokenizer.advance();
                        out.println("<expressionList>");
                        compileExpressionList();
                        out.println("</expressionList>");
                        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                            out.println("<symbol> ) </symbol>\n</term>");
                            jackTokenizer.advance();
                        }
                    }
                }
            }
            // subroutineCall: subroutineName '(' expressionList ')'
            else if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
                out.println("<symbol> ( </symbol>");
                jackTokenizer.advance();
                out.println("<expressionList>");
                compileExpressionList();
                out.println("</expressionList>");
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                    out.println("<symbol> ) </symbol>\n</term>");
                    jackTokenizer.advance();
                }
            } else out.println("</term>");
        }
        // '(' expression ')'
        else if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
            out.println("<term>\n<symbol> ( </symbol>");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                out.println("<symbol> ) </symbol>\n</term>");
                jackTokenizer.advance();
            } else {
                out.println("<expression>");
                compileExpression();
                out.println("</expression>");
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                    out.println("<symbol> ) </symbol>\n</term>");
                    jackTokenizer.advance();
                }
            }
        }
        // unaryOp '-' term
        else if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '-') {
            out.println("<term>\n<symbol> - </symbol>");
            jackTokenizer.advance();
            compileTerm();
            out.println("</term>");
        }
        // unaryOp '~' term
        else if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '~') {
            out.println("<term>\n<symbol> ~ </symbol>");
            jackTokenizer.advance();
            compileTerm();
            out.println("</term>");
        }
    }

    // Compiles a ( possibly empty) comma-separated list of expressions.
    public void compileExpressionList() {
        if (jackTokenizer.symbol() != ')') {
            out.println("<expression>");
            compileExpression();
            out.println("</expression>");
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ',') {
                out.println("<symbol> , </symbol>");
                jackTokenizer.advance();
                compileExpressionList();
            }
        }
    }
}