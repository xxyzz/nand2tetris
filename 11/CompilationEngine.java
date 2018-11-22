/******************************************************************************
 *  Compilation:  javac-algs4 CompilationEngine.java
 *  Execution:    java-algs4 CompilationEngine
 *  Dependencies: JackTokenizer.java SymbolTable.java VMWriter.java
 * 
 *  Generates the compiler's output, recursive top-down parser.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdOut;

public class CompilationEngine {
    public static final int ARGUMENT = 21, LOCAL = 22, NONE = -1, POINTER = 23, TEMP = 24, CONST = 25, THAT = 26;

    private final JackTokenizer jackTokenizer;
    private final SymbolTable symbolTable;
    private final VMWriter vmWriter;
    private final Out out;
    private String className;
    private String subroutineName;
    private int nArgs;
    private int localVariables;
    private TempTerm tempTerm;

    private class TempTerm {
        public int type;
        public int val;
        public String sVal;

        public TempTerm(int type, int val, String sVal) {
            this.type = type;
            this.val = val;
            this.sVal = sVal;
        }
    }

    // Creates a new compilation engine with the given input and output. The next routine called must be compileClass().
    public CompilationEngine(JackTokenizer jackTokenizer, Out output) {
        if (jackTokenizer == null || output == null) throw new IllegalArgumentException("Argument is null");
        this.jackTokenizer = jackTokenizer;
        this.symbolTable = new SymbolTable();
        this.vmWriter = new VMWriter(output);
        out = output;
    }

    // Compiles a complete class.
    public void compileClass() {
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.CLASS) {
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
                // out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                className = jackTokenizer.identifier();
                jackTokenizer.advance();
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '{') {
                    // out.println("<symbol> { </symbol>");
                    jackTokenizer.advance();
                    compileClassVarDec();
                }
            }
            jackTokenizer.advance();
            // if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '}') {
            //     out.println("<symbol> } </symbol>\n</class>");
            // }
            symbolTable.printClassST();
        }
    }

    private String convertTypeString(int type) {
        switch (type) {
            case JackTokenizer.INT:
                return "int";
            case JackTokenizer.CHAR:
                return "char";
            case JackTokenizer.BOOLEAN:
                return "boolean";
            case ARGUMENT:
                return "argument";
            case LOCAL:
                return "local";
            case JackTokenizer.FIELD:
                return "field";
            case JackTokenizer.STATIC:
                return "static";
            default:
                return "";
        }
    }

    // Compiles a static declaration or a field declaration.
    public void compileClassVarDec() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD &&
            (jackTokenizer.keyWord() == JackTokenizer.STATIC || jackTokenizer.keyWord() == JackTokenizer.FIELD)) {
            // out.println("<classVarDec>");
            int veriableKind = jackTokenizer.keyWord();
            if (veriableKind == JackTokenizer.STATIC) {
                // out.println("<keyword> static </keyword>");
                jackTokenizer.advance();
                String variableType = compileType();
                jackTokenizer.advance();
                compileVarName(false, veriableKind, variableType);
            }
            else if (veriableKind == JackTokenizer.FIELD) {
                // out.println("<keyword> field </keyword>");
                jackTokenizer.advance();
                String variableType = compileType();
                jackTokenizer.advance();
                compileVarName(false, veriableKind, variableType);
            }
            // if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';')
            //     out.println("<symbol> ; </symbol>");
            // out.println("</classVarDec>");

            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD &&
                (jackTokenizer.keyWord() == JackTokenizer.STATIC || jackTokenizer.keyWord() == JackTokenizer.FIELD))
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
    private String compileType() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD) {
            return convertTypeString(jackTokenizer.keyWord());
        }
        else if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            return jackTokenizer.identifier();
        }
        return "";
    }

    private void compileVarName(boolean parameterList, int variableKind, String variableType) {
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            // out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            symbolTable.define(jackTokenizer.identifier(), variableType, variableKind);
            // StdOut.println(jackTokenizer.identifier() + " " + variableType + " " + convertTypeString(variableKind));
        }
        jackTokenizer.advance();
        if (!parameterList && jackTokenizer.symbol() == ',') {
            // out.println("<symbol> , </symbol>");
            jackTokenizer.advance();
            compileVarName(false, variableKind, variableType);
        }
        else if (parameterList && jackTokenizer.symbol() == ',') {
            // out.println("<symbol> , </symbol>");
            jackTokenizer.advance();
            variableType = compileType();
            jackTokenizer.advance();
            compileVarName(true, variableKind, variableType);
        }
    }

    // Compiles a complete method, function, or constructor.
    public void compileSubroutineDec() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD) {
            int keyWord = jackTokenizer.keyWord();
            symbolTable.startSubroutine();
            if (keyWord == JackTokenizer.CONSTRUCTOR) {
                // out.println("<subroutineDec>\n<keyword> constructor </keyword>");
                jackTokenizer.advance();
                // StdOut.println("this " + className + " ARGUMENT");
                privateCompileSubroutine();
            }
            else if (keyWord == JackTokenizer.FUNCTION) {
                // out.println("<subroutineDec>\n<keyword> function </keyword>");
                jackTokenizer.advance();
                privateCompileSubroutine();
            }
            else if (keyWord == JackTokenizer.METHOD) {
                // out.println("<subroutineDec>\n<keyword> method </keyword>");
                jackTokenizer.advance();
                symbolTable.define("this", className, ARGUMENT);
                // StdOut.println("this " + className + " ARGUMENT");
                privateCompileSubroutine();
            }
        }
    }
    
    // 'void' | type
    private void privateCompileSubroutine() {
        int returnType = jackTokenizer.keyWord();
        localVariables = 0;
        if (jackTokenizer.keyWord() == JackTokenizer.VOID) {
            // out.println("<keyword> void </keyword>"); 
            jackTokenizer.advance();
            anotherPrivateSubroutine(returnType);
        }
        else if (isType(jackTokenizer.tokenType())) {
            String variableType = compileType();
            jackTokenizer.advance();
            anotherPrivateSubroutine(returnType);
        }   
    }

    private void anotherPrivateSubroutine(int returnType) {
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            // subroutineName
            // out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            subroutineName = jackTokenizer.identifier();
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
                // out.println("<symbol> ( </symbol>");
                // out.println("<parameterList>");
                jackTokenizer.advance();
                // empty parameter list
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                    // out.println("</parameterList>");
                    // out.println("<symbol> ) </symbol>");
                    // out.println("<subroutineBody>");
                    jackTokenizer.advance();
                    compileSubroutineBody();
                    // symbolTable.printSubST();
                    // out.println("</subroutineBody>");
                }
                else {
                    compileParameterList();
                    // out.println("</parameterList>");
                    if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                        // out.println("<symbol> ) </symbol>");
                        // out.println("<subroutineBody>");
                        jackTokenizer.advance();
                        compileSubroutineBody();
                        symbolTable.printSubST();
                        // out.println("</subroutineBody>");
                    }
                }
            }
            // out.println("</subroutineDec>");
            if (jackTokenizer.symbol() == '}') {
                jackTokenizer.advance();
                compileSubroutineDec();
            }
        }
    }

    // Compiles a (possibly empty) parameter list. Does not handle the enclosing ‘‘()’’.
    public void compileParameterList() {
        String variableType = compileType();
        jackTokenizer.advance();
        compileVarName(true, ARGUMENT, variableType);
    }

    // Complies a subroutine's body
    public void compileSubroutineBody() {
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '{') {
            // out.println("<symbol> { </symbol>");
            jackTokenizer.advance();
            compileVarDec();
        }
    }

    // Compiles a var declaration.
    public void compileVarDec() {
        if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD && jackTokenizer.keyWord() == JackTokenizer.VAR) {
            // out.println("<varDec>\n<keyword> var </keyword>");
            localVariables++;
            jackTokenizer.advance();
            String variableType = compileType();
            jackTokenizer.advance();
            compileVarName(false, LOCAL, variableType);

            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
                // out.println("<symbol> ; </symbol>\n</varDec>");
                jackTokenizer.advance();
                compileVarDec();
            }
        }
        else {
            // out.println("<statements>");
            vmWriter.writeFunction(className + "." + subroutineName, localVariables);
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
        // out.println("<letStatement>\n<keyword> let </keyword>");
        jackTokenizer.advance();
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            // out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '[') {
                // out.println("<symbol> [ </symbol>");
                jackTokenizer.advance();
                // out.println("<expression>");
                compileExpression();
                // out.println("</expression>");
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ']') {
                    // out.println("<symbol> ] </symbol>");
                    jackTokenizer.advance();
                    endCompileLet();
                }
            } else endCompileLet();
        }
    }

    private void endCompileLet() {
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '=') {
            // out.println("<symbol> = </symbol>");
            jackTokenizer.advance();
            // out.println("<expression>");
            compileExpression();
            // out.println("</expression>");
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
                // out.println("<symbol> ; </symbol>\n</letStatement>");
            }
            jackTokenizer.advance();
            endSubroutineBody();
        }
    }

    private void endSubroutineBody() {
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '}') {
            out.println("</statements>\n<symbol> } </symbol>");
        }
        else if (jackTokenizer.keyWord() != JackTokenizer.ELSE) compileStatements();
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
                    }
                    else {
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
        // out.println("<doStatement>\n<keyword> do </keyword>");
        jackTokenizer.advance();
        // subroutineCall: subroutineName '(' expressionList ')' | (className | varName) '.' subroutineName '(' expressionList ')'
        if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
            // out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            String functionName = jackTokenizer.identifier();
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
                }
                else if (jackTokenizer.symbol() == '.') {
                    // out.println("<symbol> . </symbol>");
                    jackTokenizer.advance();
                    if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
                        // out.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
                        String methodName = jackTokenizer.identifier();
                        jackTokenizer.advance();
                        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
                            // out.println("<symbol> ( </symbol>");
                            jackTokenizer.advance();
                            // out.println("<expressionList>");
                            nArgs = 0;
                            compileExpressionList();
                            // out.println("</expressionList>");
                            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                                // out.println("<symbol> ) </symbol>");
                                jackTokenizer.advance();
                                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ';') {
                                    // out.println("<symbol> ; </symbol>\n</doStatement>");
                                    jackTokenizer.advance();
                                    vmWriter.writeCall(functionName + "." + methodName, nArgs);
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
        }
        else {
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
        compileTerm(false);
        if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL) {
            switch (jackTokenizer.symbol()) {
                case '+':
                    // out.println("<symbol> + </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeArithmetic("add");
                    break;
                case '-':
                    // out.println("<symbol> - </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeArithmetic("sub");
                    break;
                case '*':
                    // out.println("<symbol> * </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeCall("Math.multiply", 2);
                    break;
                case '/':
                    // out.println("<symbol> / </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeCall("Math.divide", 2);
                    break;
                case '&':
                    // out.println("<symbol> &amp; </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeArithmetic("and");
                    break;
                case '|':
                    // out.println("<symbol> | </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeArithmetic("or");
                    break;
                case '<':
                    // out.println("<symbol> &lt; </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeArithmetic("lt");
                    break;
                case '>':
                    // out.println("<symbol> &gt; </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeArithmetic("gt");
                    break;
                case '=':
                    // out.println("<symbol> = </symbol>");
                    jackTokenizer.advance();
                    compileTerm(true);
                    vmWriter.writeArithmetic("eq");
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
    public void compileTerm(boolean symbol) {
        if (jackTokenizer.tokenType() == JackTokenizer.INT_CONST) {
            // out.println("<term>\n<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>\n</term>");
            if (symbol) {
                if (tempTerm.type == JackTokenizer.INT_CONST) {
                    vmWriter.writePush(CompilationEngine.CONST, tempTerm.val);
                }
                else {
                    vmWriter.writePush(symbolTable.kindOf(tempTerm.sVal), symbolTable.indexOf(tempTerm.sVal));
                }
                vmWriter.writePush(CompilationEngine.CONST, jackTokenizer.intVal());
            }
            else {
                tempTerm = new TempTerm(JackTokenizer.INT_CONST, jackTokenizer.intVal(), null);
            }
            jackTokenizer.advance(); 
        }
        else if (jackTokenizer.tokenType() == JackTokenizer.STRING_CONST) {
            // out.println("<term>\n<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>\n</term>");
            jackTokenizer.advance();
        }
        else if (jackTokenizer.tokenType() == JackTokenizer.KEYWORD) {
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
        }
        else if (jackTokenizer.tokenType() == JackTokenizer.IDENTIFIER) {
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
            }
            else out.println("</term>");
        }
        // '(' expression ')'
        else if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '(') {
            // out.println("<term>\n<symbol> ( </symbol>");
            jackTokenizer.advance();
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                // out.println("<symbol> ) </symbol>\n</term>");
                jackTokenizer.advance();
            }
            else {
                // out.println("<expression>");
                compileExpression();
                // out.println("</expression>");
                if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ')') {
                    // out.println("<symbol> ) </symbol>\n</term>");
                    jackTokenizer.advance();
                }
            }
        }
        // unaryOp '-' term
        else if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '-') {
            // out.println("<term>\n<symbol> - </symbol>");
            jackTokenizer.advance();
            compileTerm(false);
            // out.println("</term>");
        }
        // unaryOp '~' term
        else if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == '~') {
            // out.println("<term>\n<symbol> ~ </symbol>");
            jackTokenizer.advance();
            compileTerm(false);
            // out.println("</term>");
        }
    }

    // Compiles a ( possibly empty) comma-separated list of expressions.
    public void compileExpressionList() {
        if (jackTokenizer.symbol() != ')') {
            // out.println("<expression>");
            compileExpression();
            // out.println("</expression>");
            if (jackTokenizer.tokenType() == JackTokenizer.SYMBOL && jackTokenizer.symbol() == ',') {
                // out.println("<symbol> , </symbol>");
                nArgs++;
                jackTokenizer.advance();
                compileExpressionList();
            }
        }
    }
}