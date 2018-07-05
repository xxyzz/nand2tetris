/******************************************************************************
 *  Compilation:  javac-algs4 VMTranslator.java
 *  Execution:    java-algs4 VMTranslator path_to_vm_file
 *  Dependencies:
 * 
 *  Drives the process
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import java.io.File;

public class VMTranslator {
    private static void whileLoop(Parser parser, CodeWriter codeWriter) {
        while (parser.hasMoreCommands()) {
            parser.advance();
            int commandType = parser.commandType();
            switch (commandType) {
                // C_ARITHMETIC
                case Parser.C_ARITHMETIC:
                    codeWriter.writeArithmetic(parser.arg1());
                    break;
                // C_PUSH || C_POP
                case Parser.C_PUSH:
                case Parser.C_POP:
                    codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                    break;
                case Parser.C_LABEL:
                    codeWriter.writeLabel(parser.arg1());
                    break;
                case Parser.C_IF:
                    codeWriter.writeIf(parser.arg1());
                    break;
                case Parser.C_GOTO:
                    codeWriter.writeGoto(parser.arg1());
                    break;
                default:
                    break;
            }
        }
    }

    public static void main(String[] args) {
        File folder = new File(args[0]);
        if (folder.isFile()) {
            In in = new In(args[0]);
            Parser parser = new Parser(in);
            CodeWriter codeWriter = new CodeWriter(args[0]);
            codeWriter.setFileName(args[0]);
            codeWriter.writeInit();
            whileLoop(parser, codeWriter);
            codeWriter.close();
        }
        else if (folder.isDirectory()) {
            CodeWriter codeWriter = new CodeWriter(args[0]);
            codeWriter.setFileName(args[0]);
            codeWriter.writeInit();
            for (File file : folder.listFiles()) {
                In in = new In(args[0]);
                Parser parser = new Parser(in);
                whileLoop(parser, codeWriter);
            }
            codeWriter.close();
        }
    }
}