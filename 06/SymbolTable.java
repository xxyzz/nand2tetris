/******************************************************************************
 *  Compilation:  javac-algs4 SymbolTable.java
 *  Execution:    java-algs4 SymbolTable
 *  Dependencies:
 * 
 *  Keeps a correspondence between symbolic labels and numeric addresses.
 ******************************************************************************/

import edu.princeton.cs.algs4.SeparateChainingHashST;

public class SymbolTable {
    private final SeparateChainingHashST<String, Integer> hashST = new SeparateChainingHashST<>();

    public SymbolTable() {
        hashST.put("SP", 0);
        hashST.put("LCL", 1);
        hashST.put("ARG", 2);
        hashST.put("THIS", 3);
        hashST.put("THAT", 4);
        hashST.put("R0", 0);
        hashST.put("R1", 1);
        hashST.put("R2", 2);
        hashST.put("R3", 3);
        hashST.put("R4", 4);
        hashST.put("R5", 5);
        hashST.put("R6", 6);
        hashST.put("R7", 7);
        hashST.put("R8", 8);
        hashST.put("R9", 9);
        hashST.put("R10", 10);
        hashST.put("R11", 11);
        hashST.put("R12", 12);
        hashST.put("R13", 13);
        hashST.put("R14", 14);
        hashST.put("R15", 15);
        hashST.put("SCREEN", 16384);
        hashST.put("KBD", 24576);
    }

    // Adds the pair (symbol, address) to the table.
    public void addEntry(String symbol, int address) {
        hashST.put(symbol, address);
    }

    // Does the symbol table contain the given symbol ?
    public boolean contains(String symbol) {
        return hashST.contains(symbol);
    }

    // Returns the address associated with the symbol.
    public int getAddress(String symbol) {
        return hashST.get(symbol);
    }
}