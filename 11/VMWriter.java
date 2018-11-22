/******************************************************************************
 *  Compilation:  javac-algs4 VMWriter.java
 *  Execution:    java-algs4 VMWriter
 *  Dependencies: 
 * 
 *  Emits VM commands into a file, using the VM command syntax.
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.Out;

public class VMWriter {
    private final Out out;

    // Creates a new output .vm file and prepares it for writing.
    public VMWriter(Out output) {
        this.out = output;
    }

    private String convertString(int segment) {
        switch (segment) {
            case CompilationEngine.CONST:
                return "const";
            case CompilationEngine.ARGUMENT:
                return "argument";
            case CompilationEngine.LOCAL:
                return "local";
            case JackTokenizer.STATIC:
                return "static";
            case JackTokenizer.THIS:
                return "this";
            case CompilationEngine.THAT:
                return "that";
            case CompilationEngine.POINTER:
                return "pointer";
            case CompilationEngine.TEMP:
                return "temp";
            default:
                return null;
        }
    }

    /** 
     * Writes a VM push comand.
     * @param segment CONST, ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
     */ 
    public void writePush(int segment, int index) {
        out.println("push " + convertString(segment) + " " + index);
    }

    /** 
     * Writes a VM pop comand.
     * @param segment ARG, LOCAL, STATIC, THIS, THAT, POINTER, TEMP
     */
    public void writePop(int segment, int index) {
        out.println("pop " + convertString(segment) + " " + index);
    }

    /**
     * Writes a VM arithmetic command.
     * @param command ADD, SUB, NEG, EQ, GT, LT, AND, OR, NOT
     */
    public void writeArithmetic(String command) {
        out.println(command);
    }

    /**
     * Writes a VM label command.
     */
    public void writeLabel(String label) {
        out.println(label);
    }

    // Writes a VM goto command.
    public void writeGoto(String label) {
        out.println("goto " + label);
    }

    // Writes a VM if-go command.
    public void writeIf(String label) {
        out.println("if-goto " + label);
    }

    // Writes a VM call command.
    public void writeCall(String name, int nArgs) {
        out.println("call " + name + " " + nArgs);
    }

    // Writes a VM function command.
    public void writeFunction(String name, int nLocals) {
        out.println("function " + name + " " + nLocals);
    }

    // Writes a VM return command.
    public void writeReturn() {
        out.println("return");
    }

    // Closes the output file.
    public void close() {
        out.close();
    }
}