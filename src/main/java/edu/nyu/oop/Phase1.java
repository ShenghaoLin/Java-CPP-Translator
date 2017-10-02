package edu.nyu.oop;

import edu.nyu.oop.util.JavaFiveImportParser;
import xtc.tree.GNode;
import xtc.tree.Node;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Set;

public class Phase1 {

    static Set<GNode> parse(Node n) {
        NodeSet unsolved = new NodeSet();
        NodeSet solved = new NodeSet();
        unsolved.add((GNode) n);
        do {
            unsolved.addAll(JavaFiveImportParser.parse(unsolved.get(0)));
            solved.add(unsolved.remove(0));
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