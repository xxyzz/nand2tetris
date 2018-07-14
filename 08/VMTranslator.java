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
                case Parser.C_FUNCTION:
                    codeWriter.writeFunction(parser.arg1(), parser.arg2());
                    break;
                case Parser.C_RETURN:
                    codeWriter.writeReturn();
                    break;
                case Parser.C_CALL:
                    codeWriter.writeCall(parser.arg1(), parser.arg2());
                    break;
                default:
                    break;
            }
        }
    }

    public static void main(String[] args) {
        File folder = new File(args[0]);
        if (!folder.exists()) throw new IllegalArgumentException("No such file/directory");

        String fileName = folder.getName();
        if (folder.isFile()) {
            if (fileName.length() <= 3 || !fileName.substring(fileName.length() - 3).equals(".vm")) {
                throw new IllegalArgumentException(".vm file is required!");
            }
            In in = new In(args[0]);
            Parser parser = new Parser(in);
            CodeWriter codeWriter = new CodeWriter(args[0].substring(0, args[0].lastIndexOf('.')) + ".asm");
            codeWriter.setFileName(fileName);
            whileLoop(parser, codeWriter);
            codeWriter.close();
        }
        else if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files.length == 0) throw new IllegalArgumentException("Empty directory!");

            CodeWriter codeWriter = new CodeWriter(folder.getAbsolutePath() + "/" + fileName +".asm");
            codeWriter.writeInit();
            for (File file : files) {
                fileName = file.getName();
                if (fileName.length() > 3 && fileName.substring(fileName.length() - 3).equals(".vm")) {
                    In in = new In(file);
                    Parser parser = new Parser(in);
                    codeWriter.setFileName(fileName);
                    whileLoop(parser, codeWriter);
                }
            }
            codeWriter.close();
        }
    }
}