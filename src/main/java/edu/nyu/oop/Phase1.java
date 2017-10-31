/**
 * Phase 1 visitor that traverses all dependencies recrusively and adds nodes of all
 * dependencies, furthermore checks for duplciate files to avoid duplicate nodes
 * using hash list of absolute paths of files containing Java code
 *  
 * @author Shenghao Lin
 * @author Sai Akhil
 * @author Goktug Saatcioglu
 *
 * @verion 1.0
 */

package edu.nyu.oop;

import edu.nyu.oop.util.JavaFiveImportParser;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import java.nio.file.Path;
import java.nio.file.Paths;

public class Phase1 {

    /* default constructor */
    public Phase1() {}

    /** parse the Java files and their dependencies
      *
      * @param   n  Node of type Node
      * @return     List of Java ASTs
      */ 
    public static List<GNode> parse(Node n) {

        GNode node = (GNode) n;
        Set<Path> paths = new HashSet<Path>();
        List<GNode> ast = new ArrayList<GNode>();

        parse(node, paths, ast);

        return ast;
    }

    /** parse the Java files and their dependencies recursively
      *
      * @param      n  Node of type Node
      * @param  files  HashMap of files to make sure no duplicates
      * @param    ast  List of ASTs
      */
    private static void parse(GNode node, Set<Path> paths, List<GNode> ast) {

        // use a queue of nodes to find dependencies and process them
        Queue<GNode> nodes = new ArrayDeque<GNode>();
        nodes.add(node);

        while(!nodes.isEmpty()) {

            GNode next = nodes.poll();

            //test if seen to avoid cyclical dependencies
            String loc = next.getLocation().file;

            // obtain path and convert  to absolute path to ensure uniqueness
            Path path = Paths.get(loc);
            path = path.toAbsolutePath();

            // if file hasn't been visited, process it
            if(!paths.contains(path)) {
                paths.add(path);
                ast.add(next);
                nodes.addAll(JavaFiveImportParser.parse(next));
            }
        }
    }
}
