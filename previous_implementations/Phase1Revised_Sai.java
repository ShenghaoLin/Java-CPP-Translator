//@Sai
package edu.nyu.oop;

import edu.nyu.oop.util.JavaFiveImportParser;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;


public class Phase1Revised_Sai {
    private Phase1Revised_Sai() {}

    // parses the java files and returns Java ASTs
    public static List<GNode> beginParse(GNode n) {
        HashMap<String, Boolean> fileNamesFound = new HashMap<String, Boolean>();
        List<GNode> jAsts = new ArrayList<GNode>();
        beginParse(n, fileNamesFound, jAsts);
        return jAsts;
    }

    private static void beginParse(GNode n, HashMap<String, Boolean> fileNamesFound, List<GNode> jAsts) {

        //enqueue nodes and find dependencies until no files left to explore
        Queue<GNode> nodesToCheck = new ArrayDeque<GNode>();
        nodesToCheck.add(n);
        while(!nodesToCheck.isEmpty()) {

            GNode next = nodesToCheck.poll();

            //test if seen to avoid cyclical dependencies
            String loc = next.getLocation().file;
            if(fileNamesFound.get(loc) != null) {
                continue;
            }

            //if unseen, mark as seen to avoid cycles,
            //add to list of dependencies, examine all dependency children
            fileNamesFound.put(loc, true);

            jAsts.add(next);

            nodesToCheck.addAll(JavaFiveImportParser.parse(next));
        }
    }
}
