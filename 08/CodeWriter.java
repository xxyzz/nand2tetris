/******************************************************************************
 *  Compilation:  javac-algs4 CodeWriter.java
 *  Execution:    java-algs4 CodeWriter
 *  Dependencies:
 * 
 *  Writes the assembly code that implements the parsed command
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Out;
import java.io.File;

public class CodeWriter {
    private final Out out;
    private String fileName;
    private int lCommands;
    private int returnNum;

    // Opens the output file/stream and gets ready to write into it
    public CodeWriter(String inputPath) {
        lCommands = 0;
        returnNum = 0;
        out = new Out(inputPath);
    }

    /**
     * Informs the codeWriter that the translation of a new VM file has
     * started(called by the main program of the VM translator)
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     *  Writes the assembly instructios that effect the bootstrap code that
     *  initialize the VM. This code must be placed at the beginning of the
     *  generated *.asm file
     */
    public void writeInit() {
        // Initialize the stack pointer to 0x0100
        out.println("@256\nD=A\n@SP\nM=D");
        // Start executing (the translated code of ) Sys.init
        writeCall("Sys.init", 0);
    }

    // Writes assembly code that effects the label command
    public void writeLabel(String label) {
        out.println("(" + label +")");
    }

    // Writes assembly code that effects the goto command
    public void writeGoto(String label) {
        out.println("@" + label + "\n0;JMP");
    }

    // Writes assembly code that effects the if-goto command
    public void writeIf(String label) {
        out.println("// write if goto");
        out.println("@SP\nAM=M-1\n" +
                    "D=M\n" +
                    "A=A-1\n" +
                    "@" + label +
                    "\nD;JNE");
    }

    // Writes assembly code that effects the function command
    public void writeFunction(String functionName, int numVars) {
        out.println("// write function " + functionName);
        writeLabel(functionName);
        for (int i = 0; i < numVars; i++) {
            writePushPop(Parser.C_PUSH, "constant", 0);
        }
    }

    // Writes assembly code that effects the call command
    public void writeCall(String functionName, int numVars) {
        out.println("// write call");
        writePop("return", returnNum);
        writePop("local", 0);
        writePop("argument", 0);
        writePop("this", 0);
        writePop("that", 0);
        // ARG = SP - 5 - nArgs
        out.println("@" + (numVars + 5) +
                    "\nD=A\n" +
                    "@SP\n" +
                    "D=M-D\n" +
                    "@ARG\n" +
                    "M=D");
        // LCL = SP
        out.println("@SP\n" +
                    "D=M\n" +
                    "@LCL\n" +
                    "M=D");
        writeGoto(functionName);
        writeLabel("RETURN" + returnNum++);
    }

    // Writes assembly code that effects the return command
    //      SP LCL ARG THIS THAT
    // ROM  0  1   2   3    4
    public void writeReturn() {
        out.println("// write return");
        // FRAME = LCL
        // writePushPop() uses R13, so use R14 here
        out.println("@LCL\n" +
                    "D=M\n" +
                    "@R14\n" +
                    "M=D");

        // RET = *(FRAME - 5)
        // all return commands in a function return to the same return address, so don't use returnNum here.
        out.println("@5\n" +
                    "A=D-A\n" +
                    "D=M\n" +
                    "@R15\n" +
                    "M=D");

        // *ARG = pop()
        writePushPop(Parser.C_POP, "argument", 0);

        // SP = ARG+1
        out.println("@ARG\n" +
                    "D=M\n" +
                    "@SP\n" +
                    "M=D+1");

        // THAT = *(FRAME-1)
        out.println("@R14\n" +
                    "A=M-1\n" +
                    "D=M\n" +
                    "@THAT\n" +
                    "M=D");

        // THIS = *(FRAME-2)
        out.println("@R14\n" +
                    "D=M\n" +
                    "@2\n" +
                    "A=D-A\n" +
                    "D=M\n" +
                    "@THIS\n" +
                    "M=D");

        // ARG = *(FRAME-3)
        out.println("@R14\n" +
                    "D=M\n" +
                    "@3\n" +
                    "A=D-A\n" +
                    "D=M\n" +
                    "@ARG\n" +
                    "M=D");

        // LCL = *(FRAME-4)
        out.println("@R14\n" +
                    "D=M\n" +
                    "@4\n" +
                    "A=D-A\n" +
                    "D=M\n" +
                    "@LCL\n" +
                    "M=D");

        // goto RET
        out.println("@R15\nA=M\n0;JMP");
    }

    /** Writes to the output file the assembly code that implements the given arithmetic command.
     * true in VM: -1 0xFFFF 1111111111111111
     * false in VM: 0 0x0000 0000000000000000
     */
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

    // pop commands for writeCall()
    private void writePop(String segment, int index) {
        switch (segment) {
            case "argument":
                out.println("@ARG\nD=M");
                break;
            case "local":
                out.println("@LCL\nD=M");
                break;
            case "this":
                out.println("@THIS\nD=M");
                break;
            case "that":
                out.println("@THAT\nD=M");
                break;
            case "return":
                out.println("@RETURN" + index + "\nD=A");
                break;
            default:
                break;
        }
        out.println("@SP\n" +
                    "AM=M+1\n" +
                    "A=A-1\n" +
                    "M=D");
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