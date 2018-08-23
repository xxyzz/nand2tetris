/******************************************************************************
 *  Compilation:  javac-algs4 JackAnalyzer.java
 *  Execution:    java-algs4 JackAnalyzer
 *  Dependencies:
 * 
 *  top-level driver that sets up and invokes the other modules
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import java.io.File;

public class JackAnalyzer {

    public static void whileLoop(JackTokenizer jackTokenizer, Out tokensXML) {
        tokensXML.println("<tokens>");
        while (jackTokenizer.hasMoreTokens()) {
            int tokenType = jackTokenizer.tokenType();
            if (tokenType == JackTokenizer.KEYWORD) {
                tokensXML.print("<keyword> ");
                switch (jackTokenizer.keyWord()) {
                    case 0:
                        tokensXML.print("class");
                        break;
                    case 1:
                        tokensXML.print("method");
                        break;
                    case 2:
                        tokensXML.print("function");
                        break;
                    case 3:
                        tokensXML.print("constructor");
                        break;
                    case 4:
                        tokensXML.print("int");
                        break;
                    case 5:
                        tokensXML.print("boolean");
                        break;
                    case 6:
                        tokensXML.print("char");
                        break;
                    case 7:
                        tokensXML.print("void");
                        break;
                    case 8:
                        tokensXML.print("var");
                        break;
                    case 9:
                        tokensXML.print("static");
                        break;
                    case 10:
                        tokensXML.print("field");
                        break;
                    case 11:
                        tokensXML.print("let");
                        break;
                    case 12:
                        tokensXML.print("do");
                        break;
                    case 13:
                        tokensXML.print("if");
                        break;
                    case 14:
                        tokensXML.print("else");
                        break;
                    case 15:
                        tokensXML.print("while");
                        break;
                    case 16:
                        tokensXML.print("return");
                        break;
                    case 17:
                        tokensXML.print("true");
                        break;
                    case 18:
                        tokensXML.print("false");
                        break;
                    case 19:
                        tokensXML.print("null");
                        break;
                    case 20:
                        tokensXML.print("this");
                        break;
                    default:
                        break;
                }
                tokensXML.println(" </keyword>");
            }
            if (tokenType == JackTokenizer.SYMBOL) {
                tokensXML.print("<symbol> ");
                if (jackTokenizer.symbol() == '<') tokensXML.print("&lt;");
                else if (jackTokenizer.symbol() == '>') tokensXML.print("&gt;");
                else if (jackTokenizer.symbol() == '"') tokensXML.print("&quot;");
                else if (jackTokenizer.symbol() == '&') tokensXML.print("&amp;"); 
                else tokensXML.print(jackTokenizer.symbol());
                tokensXML.println(" </symbol>");
            }
            if (tokenType == JackTokenizer.IDENTIFIER) tokensXML.println("<identifier> " + jackTokenizer.identifier() + " </identifier>");
            if (tokenType == JackTokenizer.INT_CONST) tokensXML.println("<integerConstant> " + jackTokenizer.intVal() + " </integerConstant>");
            if (tokenType == JackTokenizer.STRING_CONST) tokensXML.println("<stringConstant> " + jackTokenizer.stringVal() + " </stringConstant>");
        }
        tokensXML.println("</tokens>");
        tokensXML.close();
    }

    public static void main(String[] args) {
        File folder = new File(args[0]);
        if (!folder.exists()) throw new IllegalArgumentException("No such file/directory");

        String fileName = folder.getName();
        if (folder.isFile()) {
            if (fileName.length() <= 5 || !fileName.substring(fileName.length() - 5).equals(".jack")) {
                throw new IllegalArgumentException(".jack file is required!");
            }
            In in = new In(args[0]);
            JackTokenizer jackTokenizer = new JackTokenizer(in);
            String filePath = args[0].substring(0, args[0].lastIndexOf('.'));
            Out tokensXMl = new Out(filePath + "TT.xml");
            whileLoop(jackTokenizer, tokensXMl);

            // In xmlInput = new In(filePath + "TT.xml");
            // Out compilerOutput = new Out(filePath + "_.xml");
            // CompilationEngine compilationEngine = new CompilationEngine(xmlInput, compilerOutput);
        }
        else if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files.length == 0) throw new IllegalArgumentException("Empty directory!");

            for (File file : files) {
                fileName = file.getName();
                if (fileName.length() > 5 && fileName.substring(fileName.length() - 5).equals(".jack")) {
                    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                    In in = new In(file);
                    JackTokenizer jackTokenizer = new JackTokenizer(in);
                    Out tokensXML = new Out(folder.getAbsolutePath() + "/" + fileName + "TT.xml");
                    whileLoop(jackTokenizer, tokensXML);

                    // In xmlInput = new In(fileName + "TT.xml");
                    // Out compilerOutput = new Out(fileName + "_.xml");
                    // CompilationEngine compilationEngine = new CompilationEngine(xmlInput, compilerOutput);
                }
            }
        }
    }
}