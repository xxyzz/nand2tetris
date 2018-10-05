/******************************************************************************
 *  Compilation:  javac-algs4 SymbolTable.java
 *  Execution:    java-algs4 SymbolTable
 *  Dependencies: 
 * 
 *  Provides a symbol table abstraction. The symbol table associates the identifier
 *  names found in the program with identifier properties needed for compilation:
 *  type, kind, and running index. The symbol table for Jack programs has two nested
 *  scopes (class/subroutine).
 *
 ******************************************************************************/

import java.util.HashMap;
import java.util.Map;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdOut;

public class SymbolTable {

    private final HashMap<String, Variable> classST;
    private final HashMap<String, Variable> subroutineST;

    private final HashMap<Integer, Integer> classVeriabliesCount;
    private final HashMap<Integer, Integer> subroutineVeriablesCount;

    private class Variable {
        public String type;
        public int kind;
        public int index;

        public Variable(String type, int kind) {
            this.type = type;
            this.kind = kind;
            this.index = varCount(kind);
        }
    }

    // Creates a new symbol table
    public SymbolTable() {
        classST = new HashMap<String, Variable>();
        subroutineST = new HashMap<String, Variable>();
        classVeriabliesCount = new HashMap<Integer, Integer>();
        subroutineVeriablesCount = new HashMap<Integer, Integer>();
    }

    // Strats a new subroutine scope(i.e., resets the subroutine's symbol table)
    public void startSubroutine() {
        subroutineST.clear();
        subroutineVeriablesCount.clear();
    }

    /**
     * Defines a new identifier of the given name, type and kind, and assigns it a
     * running index. STATIC and FIELD identifiers have a class scope, while ARG
     * and VAR identifiers have a subroutine scope.
     * 
     * @param type INT, CHAR, BOOLEAN, CLASSNAME
     * @param kind FIELD, STATIC, LOCAL, ARGUMENT
     */
    public void define(String name, String type, int kind) {
        if (name == null || type == null || kind < 0) throw new IllegalArgumentException();
        switch (kind) {
            case JackTokenizer.STATIC:
            case JackTokenizer.FIELD:
                classST.put(name, new Variable(type, kind));
                if (classVeriabliesCount.containsKey(kind)) {
                    classVeriabliesCount.put(kind, classVeriabliesCount.get(kind) + 1);
                }
                else {
                    classVeriabliesCount.put(kind, 1);
                }
                break;
            default:
                subroutineST.put(name, new Variable(type, kind));
                if (subroutineVeriablesCount.containsKey(kind)) {
                    subroutineVeriablesCount.put(kind, subroutineVeriablesCount.get(kind) + 1);
                }
                else {
                    subroutineVeriablesCount.put(kind, 1);
                }
                break;
        }
    }

    // Returns the number of variables of the given kind already defined in the current scope.
    public int varCount(int kind) {
        if (kind < 0) return 0;
        if (subroutineVeriablesCount.containsKey(kind)) {
            return subroutineVeriablesCount.get(kind);
        }
        if (classVeriabliesCount.containsKey(kind)) {
            return classVeriabliesCount.get(kind);
        }
        return 0;
    }

    /** 
     * Returns the kind of the named identifier in the current scope. If the identifier
     * is unknown in the current scope, return NONE.
    */
    public int kindOf(String name) {
        if (subroutineST.containsKey(name)) {
            return subroutineST.get(name).kind;
        }
        if (classST.containsKey(name)) {
            return classST.get(name).kind;
        }
        return CompilationEngine.NONE;
    }

    // Returns the type of the named identifier in the current scope.
    public String typeOf(String name) {
        if (subroutineST.containsKey(name)) {
            return subroutineST.get(name).type;
        }
        if (classST.containsKey(name)) {
            return classST.get(name).type;
        }
        return "";
    }

    // Returns the index assigned to the named identifier.
    public int indexOf(String name) {
        if (subroutineST.containsKey(name)) {
            return subroutineST.get(name).index;
        }
        if (classST.containsKey(name)) {
            return classST.get(name).index;
        }
        return -1;
    }

    // test
    public void printClassST() {
        StdOut.println("classST:");
        for (Map.Entry<String, Variable> entry : classST.entrySet()) {
            StdOut.println("name: " + entry.getKey() + " type: " + entry.getValue().type +
            " kind: " + convertTypeString(entry.getValue().kind) + " index: " + entry.getValue().index);
        }
    }
    
    // test
    public void printSubST() { 
        StdOut.println("subroutinST:");
        for (Map.Entry<String, Variable> entry : subroutineST.entrySet()) {
            StdOut.println("name: " + entry.getKey() + " type: " + entry.getValue().type +
            " kind: " + convertTypeString(entry.getValue().kind) + " index: " + entry.getValue().index);
        }
    }

    // test
    private String convertTypeString(int type) {
        switch (type) {
            case JackTokenizer.INT:
                return "int";
            case JackTokenizer.CHAR:
                return "char";
            case JackTokenizer.BOOLEAN:
                return "boolean";
            case CompilationEngine.ARGUMENT:
                return "argument";
            case CompilationEngine.LOCAL:
                return "local";
            case JackTokenizer.FIELD:
                return "field";
            case JackTokenizer.STATIC:
                return "static";
            default:
                return "";
        }
    }

    // test
    public static void main(String[] args) {
        In in = new In(args[0]);
        JackTokenizer jackTokenizer = new JackTokenizer(in);
        String filePath = args[0].substring(0, args[0].lastIndexOf('.'));
        Out outFile = new Out(filePath + ".vm");
        CompilationEngine compilationEngine = new CompilationEngine(jackTokenizer, outFile);
        compilationEngine.compileClass();
        in.close();
        outFile.close();
    }
}