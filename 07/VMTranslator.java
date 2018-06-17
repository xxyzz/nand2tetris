/******************************************************************************
 *  Compilation:  javac-algs4 VMTranslator.java
 *  Execution:    java-algs4 VMTranslator path_to_vm_file
 *  Dependencies:
 * 
 *  Drives the process
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;

public class VMTranslator {
    public static void main(String[] args) {
        In in = new In(args[0]);
        if (!in.isEmpty()) {
            Parser parser = new Parser(in);
            CodeWriter codeWriter = new CodeWriter(args[0]);
            while (parser.hasMoreCommands()) {
                parser.advance();
                int commandType = parser.commandType();
                switch (commandType) {
                    // C_ARITHMETIC
                    case 0:
                        codeWriter.writeArithmetic(parser.arg1());
                        break;
                    // C_PUSH || C_POP
                    case 1:
                    case 2:
                        codeWriter.writePushPop(commandType, parser.arg1(), parser.arg2());
                        break;
                    default:
                        break;
                }
            }
            codeWriter.close();
        }
    }
}