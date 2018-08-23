/******************************************************************************
 *  Compilation:  javac-algs4 JackAnalyzer.java
 *  Execution:    java-algs4 JackAnalyzer
 *  Dependencies: JackTokenizer.java CompilationEngine.java 
 * 
 *  top-level driver that sets up and invokes the other modules
 *
 ******************************************************************************/

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import java.io.File;

public class JackAnalyzer {
    public static void main(String[] args) {
        File folder = new File(args[0]);
        if (!folder.exists()) throw new IllegalArgumentException("No such file/directory");

        String fileName = folder.getName();
        if (folder.isFile()) {
            if (fileName.length() <= 5 || !fileName.substring(fileName.length() - 5).equals(".jack")) {
                throw new IllegalArgumentException(".jack file is required!");
            }
            In in = new In(args[0]);
            JackTokenizer jackTokenizer = new JackTokenizer(in);
            String filePath = args[0].substring(0, args[0].lastIndexOf('.'));
            Out outFile = new Out(filePath + "_.xml");
            CompilationEngine compilationEngine = new CompilationEngine(jackTokenizer, outFile);
            compilationEngine.compileClass();
            in.close();
            outFile.close();
        }
        else if (folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files.length == 0) throw new IllegalArgumentException("Empty directory!");

            for (File file : files) {
                fileName = file.getName();
                if (fileName.length() > 5 && fileName.substring(fileName.length() - 5).equals(".jack")) {
                    fileName = fileName.substring(0, fileName.lastIndexOf('.'));
                    In in = new In(file);
                    JackTokenizer jackTokenizer = new JackTokenizer(in);
                    Out outFile = new Out(folder.getAbsolutePath() + "/" + fileName + "_.xml");
                    CompilationEngine compilationEngine = new CompilationEngine(jackTokenizer, outFile);
                    compilationEngine.compileClass();
                    in.close();
                    outFile.close();
                }
            }
        }
    }
}