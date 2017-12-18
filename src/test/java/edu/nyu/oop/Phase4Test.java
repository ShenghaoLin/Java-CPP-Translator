package edu.nyu.oop;


import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Runtime;

import java.util.*;

import static org.junit.Assert.*;

public class Phase4Test {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(Phase1Test.class);

    private static final Runtime runtime = new Runtime();
    private static HashMap<String, String> childrenToParents = new HashMap<String, String>();
    private static HashMap<String, ArrayList<Phase1.Initializer>> inits = new HashMap<String, ArrayList<Phase1.Initializer>>();

    private static Node node;// = null;

    private static String testFile = "050";

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase4Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test"+testFile+"/Test"+testFile+".java");
        List<GNode> javaAsts = Phase1.parse(node);

        Phase4 phase4 = new Phase4(runtime, childrenToParents, inits);
        ArrayList<Node> cppAsts = new ArrayList<Node>();

        for (Node javaAst : javaAsts) {
            Phase2 phase2 = new Phase2();
            Node cppAst = Phase2.runPhase2(javaAst);
            cppAsts.add(cppAst);
            childrenToParents.putAll(Phase2.childrenToParents);
        }
    }

    @Test
    public void testInheritance() { //throws xtc.tree.VisitingException{

        Object[] keySet = childrenToParents.keySet().toArray();

        assertTrue("DERIVED CLASS", keySet[0].toString().equals("A")
                                            || keySet[0].toString().equals("Test"+testFile)
                                            || keySet[0].toString().equals("B2"));
        assertTrue("Base 2", keySet[1].toString().equals("Test"+testFile)
                                            || keySet[1].toString().equals("Class")
                                            || keySet[1].toString().equals("B")
                                            || keySet[1].toString().equals("A"));
        assertTrue("Base 1", keySet[2].toString().equals("Class")
                                            || keySet[2].toString().equals("String")
                                            || keySet[2].toString().equals("Test"+testFile)
                                            || keySet[2].toString().equals("A")
                                            || keySet[2].toString().equals("B")
                                            || keySet[2].toString().equals("C"));
       if(keySet.length > 3) {
           assertTrue("Base 0", keySet[3].toString().equals("String")
                   || keySet[3].toString().equals("Class")
                   || keySet[3].toString().equals("Test" + testFile)
                   || keySet[3].toString().equals("C"));
           if (keySet.length > 4) {
               assertTrue("Base .", keySet[4].toString().equals("String")
                       || keySet[4].toString().equals("Test" + testFile)
                       || keySet[4].toString().equals("Class"));
               if (keySet.length > 5) {
                   assertTrue("Base .", keySet[5].toString().equals("String")
                           || keySet[5].toString().equals("Test" + testFile)
                           || keySet[5].toString().equals("Class"));
                   if (keySet.length > 6) {
                       assertTrue("Base .", keySet[6].toString().equals("String")
                               || keySet[6].toString().equals("Test" + testFile)
                               || keySet[6].toString().equals("Class")
                               || keySet[6].toString().equals("B1"));
                   }
               }
           }
       }
    }
}
