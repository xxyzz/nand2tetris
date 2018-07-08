/******************************************************************************
 *  Compilation:  javac-algs4 CodeWriter.java
 *  Execution:    java-algs4 CodeWriter
 *  Dependencies:
 * 
 *  Writes the assembly code that implements the parsed command
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Out;

public class CodeWriter {
    private final Out out;
    private final String fileName;
    private int lCommands;

    // Opens the output file/stream and gets ready to write into it
    public CodeWriter(String inputPath) {
        fileName = inputPath.substring(inputPath.lastIndexOf('/') + 1, inputPath.lastIndexOf('.'));
        out = new Out(inputPath.substring(0, inputPath.lastIndexOf('.')) + ".asm");
        lCommands = 0;
    }

    // Writes to the output file the assembly code that implements the given arithmetic command.
    // true in VM: -1 0xFFFF 1111111111111111
    // false in VM: 0 0x0000 0000000000000000
    public void writeArithmetic(String command) {
        printArithmeticComment(command);
        out.println("@SP\nAM=M-1");
        switch (command) {
            case "add":
                out.println("D=M\nA=A-1\nM=M+D");
                break;
            case "sub":
                out.println("D=M\nA=A-1\nM=M-D");
                break;
            case "neg":
                out.println("M=-M\n@SP\nAM=M+1");
                break;
            case "eq":
                out.println("D=M\n" +
                            "A=A-1\n" +
                            "D=M-D\n" +
                            "M=0\n" +
                            "@END_EQ" + lCommands +
                            "\nD;JNE\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=-1\n" +
                            "(END_EQ" + lCommands + ")");
                lCommands++;
                break;
            case "gt":
                out.println("D=M\n" +
                            "A=A-1\n" +
                            "D=M-D\n" +
                            "M=0\n" +
                            "@END_GT" + lCommands +
                            "\nD;JLE\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=-1\n" +
                            "(END_GT" + lCommands + ")");
                lCommands++;
                break;
            case "lt":
                out.println("D=M\n" +
                            "A=A-1\n" +
                            "D=M-D\n" +
                            "M=0\n" +
                            "@END_LT" + lCommands +
                            "\nD;JGE\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=-1\n" +
                            "(END_LT" + lCommands + ")");
                lCommands++;
                break;
            case "and":
                out.println("D=M\nA=A-1\nM=M&D");
                break;
            case "or":
                out.println("D=M\nA=A-1\nM=M|D");
                break;
            case "not":
                out.println("M=!M\n@SP\nAM=M+1");
                break;
            default:
                break;
        }
    }

    /**
     *  Writes to the output file the assembly code that implements the given arithmetic command,
     *  where command is either C_PUSH or C_POP.
     */
    public void writePushPop(int command, String segment, int index) {
        printPushPopComment(command, segment, index);
        // push
        if (command == Parser.C_PUSH) {
            switch (segment) {
                case "argument":
                    out.println("@ARG\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "A=A+D\n" +
                                "D=M");
                    break;
                case "local":
                    out.println("@"+ index + "\n" +
                                "D=A\n" +
                                "@LCL\n" +
                                "A=M+D\n" +
                                "D=M");
                    break;
                case "static":
                    out.println("@" + fileName +
                                "." + index + "\n" +
                                "D=M");
                    break;
                case "constant":
                    out.println("@" + index +"\n" +
                                "D=A");
                    break;
                case "this":
                    out.println("@THIS\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "A=D+A\n" +
                                "D=M");
                    break;
                case "that":
                    out.println("@THAT\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "A=D+A\n" +
                                "D=M");
                    break;
                case "pointer":
                    out.println("@" + (index + 3) + "\n" +
                                "D=M");
                    break;
                case "temp":
                    out.println("@" + (index + 5) + "\n" +
                                "D=M");
                    break;
                default:
                    break;
            }
            out.println("@SP\n" +
                        "AM=M+1\n" +
                        "A=A-1\n" +
                        "M=D");
        }
        // pop
        if (command == Parser.C_POP) {
            switch (segment) {
                case "argument":
                    out.println("@ARG\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "D=A+D");
                    break;
                case "local":
                    out.println("@"+ index + "\n" +
                                "D=A\n" +
                                "@LCL\n" +
                                "D=M+D");
                    break;
                case "static":
                    out.println("@" + fileName +
                                "." + index + "\n" +
                                "D=A");
                    break;
                case "constant":
                    out.println("@" + index + "\nD=A");
                    break;
                case "this":
                    out.println("@THIS\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "D=D+A");
                    break;
                case "that":
                    out.println("@THAT\n" +
                                "D=M\n" +
                                "@" + index + "\n" +
                                "D=D+A");
                    break;
                case "pointer":
                    out.println("@" + (index + 3) + "\nD=A");
                    break;
                case "temp":
                    out.println("@" + (index + 5) + "\nD=A");
                    break;
                default:
                    break;
            }
            out.println("@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D");
        }
    }

    // Closes the output file.
    public void close() {
        out.close();
    }

    private void printArithmeticComment(String command) {
        out.println("// " + command);
    }

    private void printPushPopComment(int command, String segment, int index) {
        if (command == Parser.C_PUSH) {
            out.println("// push " + segment + " " + index);
        }
        else {
            out.println("// pop " + segment + " " + index);
        }
    }
}