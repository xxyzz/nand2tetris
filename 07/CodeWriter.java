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

    // Opens the output file/stream and gets ready to write into it
    public CodeWriter(String inputPath) {
        fileName = inputPath.substring(0, inputPath.lastIndexOf('.'));
        out = new Out(fileName + ".asm");
    }

    // Writes to the output file the assembly code that implements the given arithmetic command.
    public void writeArithmetic(String command) {
        printArithmeticCommant(command);
        switch (command) {
            case "add":
                out.println("@SP\nAM=M-1\nD=M\nA=A-1\nM=M+D\nA=A+1");
                break;
            case "sub":
                out.println("@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D\nA=A+1");
                break;
            case "neg":
                out.println("@SP\nAM=M-1\nM=-M\nA=A+1");
                break;
            case "eq":
                out.println("@SP\n" +
                            "AM=M-1\n" +
                            "D=M\n" +
                            "A=A-1\n" +
                            "D=M-D\n" +
                            "M=0\n" +
                            "@END_EQ\n" +
                            "D;JNE\n" +
                            "@SP\n" +
                            "A=M-1\n" +
                            "M=-1\n" +
                            "(END_EQ)\n" +
                            "@SP\n" +
                            "A=A+1");
                break;
            case "gt":
                break;
            case "lt":
                break;
            case "and":
                out.println("@SP\nA=M\nD=M\nA=A-1\nM=M&D");
                break;
            case "or":
                out.println("@SP\nA=M\nD=M\nA=A-1\nM=M|D");
                break;
            case "not":
                out.println("@SP\nA=M\nM=!M");
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
        printPushPopCommant(command, segment, index);
        // push
        if (command == 1) {
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
        if (command == 2) {
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
                    out.println("D=" + index);
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
                    out.println("D=" + (index + 3));
                    break;
                case "temp":
                    out.println("D=" + (index + 5));
                    break;
                default:
                    break;
            }
            out.println("@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "AM=M-1\n" +
                        "D=M\n" +
                        "@13\n" +
                        "A=M\n" +
                        "M=D");
        }
    }

    // Closes the output file.
    public void close() {
        out.close();
    }

    private void printArithmeticCommant(String command) {
        out.println("// " + command);
    }

    private void printPushPopCommant(int command, String segment, int index) {
        if (command == 1) {
            out.println("// push " + segment + " " + index);
        }
        else {
            out.println("// pop " + segment + " " + index);
        }
    }
}