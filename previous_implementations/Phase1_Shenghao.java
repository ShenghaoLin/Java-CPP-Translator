package edu.nyu.oop;

import edu.nyu.oop.util.JavaFiveImportParser;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;
import java.util.List;

public class Phase1_Shenghao {

	/* Reading import class repeatedly until all imported files (including 
	 * files imported by imported files), and return a set of GNode instances
	 * corrsponding them.
	 */
    static Set<GNode> parse(Node n) {

        NodeSet unsolved = new NodeSet();
        NodeSet solved = new NodeSet();
        List<GNode> tmp;

        unsolved.add((GNode) n);
        do {
            tmp = JavaFiveImportParser.parse(unsolved.get(0));
            for (Object o : tmp) {
                if ((!unsolved.contains(o))&&(!solved.contains(o))) {
                    unsolved.add((GNode) o);
                }
            }
            solved.add(unsolved.remove(0));

        //Loop until there is no unsolved files
        } while (!unsolved.isEmpty());

        return solved;
    }

}

class NodeSet extends LinkedList<GNode> implements Set<GNode> {
	
    @Override
    public boolean add(GNode gNode) {
        if (this.contains(gNode)){
            return false;
        }
        return super.add(gNode);
    }

    @Override
    public boolean addAll(Collection<? extends GNode> collection) {
        for (GNode n : collection) {
            this.add(n);
        }
        return true;
    }
}