/******************************************************************************
 *  Compilation:  javac-algs4 Assembler.java
 *  Execution:    java-algs4 Assembler add/Add.asm
 *  Dependencies: Parser.java Code.java
 *
 *  The Hack assembler reads as input a text file named Prog.asm, containing a Hack
 *  assembly program, and produces as output a text file named Prog.hack,
 *  containing the translated Hack machine code.
 *
 ******************************************************************************/
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Assembler {
    public Assembler() { }

    public static void main(String[] args) {
        In in = new In(args[0]);
        while (!in.isEmpty()) {
            Parser parser = new Parser(in);
            parser.advance();
            StdOut.println("comandtype: " + parser.commandType());
            StdOut.println("symbol: " + parser.symbol());
            StdOut.println("dest: " + parser.dest());
            StdOut.println("comp: " + parser.comp());
            StdOut.println("jump: " + parser.jump());
        }
    }
}