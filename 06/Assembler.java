/******************************************************************************
 *  Compilation:  javac-algs4 Assembler.java
 *  Execution:    java-algs4 Assembler add/Add.asm
 *  Dependencies: Parser.java Code.java SymbolTable.java
 *
 *  The Hack assembler reads as input a text file named Prog.asm, containing a Hack
 *  assembly program, and produces as output a text file named Prog.hack,
 *  containing the translated Hack machine code.
 *
 ******************************************************************************/
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
// import edu.princeton.cs.algs4.StdOut;

public class Assembler {
    public Assembler() { }

    public static void main(String[] args) {
        In in = new In(args[0]);
        int n = 0;
        int m = 16;
        Out out = new Out(args[0].substring(0, args[0].lastIndexOf('.')) + ".hack");
        SymbolTable symbolTable = new SymbolTable();
        
        // Handling label symbols
        while (!in.isEmpty()) {
            Parser parser = new Parser(in);
            parser.advance();
            String symbol = parser.symbol();
            if (parser.commandType() == 2 && !symbolTable.contains(symbol)) {
                symbolTable.addEntry(symbol, n);
                n--;
            }
            n++;
        }

        in = new In(args[0]);
        while (!in.isEmpty()) {
            Parser parser = new Parser(in);
            parser.advance();
            if (parser.commandType() == 0) {
                String symbol = parser.symbol();
                if (symbol.matches("^\\d+$")) {
                    out.println(String.format("%16s", Integer.toBinaryString(Integer.parseInt(symbol))).replace(' ', '0'));
                }
                else {
                    // Add variable symbols
                    if (!symbolTable.contains(symbol)) {
                        symbolTable.addEntry(symbol, m);
                        m++;
                    }
                    out.println(String.format("%16s", Integer.toBinaryString(symbolTable.getAddress(symbol))).replace(' ', '0'));
                }
            }
            else if (parser.commandType() == 1) {
                Code code = new Code(parser.dest(), parser.comp(), parser.jump());
                out.println("111" + code.comp() + code.dest() + code.jump());
            }
        }
        out.close();
    }
}