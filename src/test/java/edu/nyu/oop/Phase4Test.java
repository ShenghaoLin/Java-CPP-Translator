package edu.nyu.oop;

import java.io.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import scala.collection.immutable.Stream;
import xtc.tree.GNode;
import xtc.tree.Node;
import xtc.util.Runtime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.junit.Assert.*;

public class Phase4Test {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(Phase1Test.class);

    protected final Runtime runtime = new Runtime();

    private static Node node;// = null;

    @BeforeClass
    public static void beforeClass() {
        logger.debug("Executing Phase4Test");
        node = XtcTestUtils.loadTestFile("src/test/java/inputs/test012/Test012.java");
    }

    @Test
    public void testProcess() { //throws xtc.tree.VisitingException{
        List<GNode> javaAsts = Phase1.parse(node);

        List<GNode> l = Phase4.process(javaAsts);

//        for(GNode g: l){
//            runtime.console().format(g).pln().flush();
//        }

        assertTrue("Process", l.get(0).get(0).toString().equals("PackageDeclaration(null, QualifiedIdentifier(\"inputs\", \"test012\"))"));
        assertTrue("A", l.get(0).get(1).toString().equals("ClassDeclaration(Modifiers(), \"A\", null, null, null, ClassBody(FieldDeclaration(Modifiers(), Type(QualifiedIdentifier(\"String\"), null), Declarators(Declarator(\"a\", null, null))), MethodDeclaration(Modifiers(), null, VoidType(), \"__A::setA\", FormalParameters(FormalParameter(Modifier(), Type(QualifiedIdentifier(\"A\"), null), null, \"__this\", null), FormalParameter(Modifiers(), Type(QualifiedIdentifier(\"String\"), null), null, \"x\", null)), null, null, Block(ExpressionStatement(Expression(PrimaryIdentifier(\"__this -> a\"), \"=\", PrimaryIdentifier(\"x\"))))), MethodDeclaration(Modifiers(), null, VoidType(), \"__A::printOther\", FormalParameters(FormalParameter(Modifier(), Type(QualifiedIdentifier(\"A\"), null), null, \"__this\", null), FormalParameter(Modifiers(), Type(QualifiedIdentifier(\"A\"), null), null, \"other\", null)), null, null, Block(ExpressionStatement(CallExpression(null, null, \"cout\", Arguments(CallExpression(PrimaryIdentifier(\"other\"), null, \"-> __vptr -> myToString\", Arguments(PrimaryIdentifier(\"other\"))), \"endl\"))))), MethodDeclaration(Modifiers(), null, Type(QualifiedIdentifier(\"String\"), null), \"__A::myToString\", FormalParameters(FormalParameter(Modifier(), Type(QualifiedIdentifier(\"A\"), null), null, \"__this\", null)), null, null, Block(ReturnStatement(PrimaryIdentifier(\"__this -> a\"))))))"));
        assertTrue("B1", l.get(0).get(2).toString().equals("ClassDeclaration(Modifiers(), \"B1\", null, Extension(Type(QualifiedIdentifier(\"A\"), null)), null, ClassBody(FieldDeclaration(Modifiers(), Type(QualifiedIdentifier(\"String\"), null), Declarators(Declarator(\"b\", null, null)))))"));
        assertTrue("B2", l.get(0).get(3).toString().equals("ClassDeclaration(Modifiers(), \"B2\", null, Extension(Type(QualifiedIdentifier(\"A\"), null)), null, ClassBody(FieldDeclaration(Modifiers(), Type(QualifiedIdentifier(\"String\"), null), Declarators(Declarator(\"b\", null, null)))))"));
        assertTrue("C", l.get(0).get(4).toString().equals("ClassDeclaration(Modifiers(), \"C\", null, Extension(Type(QualifiedIdentifier(\"B1\"), null)), null, ClassBody(FieldDeclaration(Modifiers(), Type(QualifiedIdentifier(\"String\"), null), Declarators(Declarator(\"c\", null, null))), MethodDeclaration(Modifiers(), null, Type(QualifiedIdentifier(\"String\"), null), \"__C::myToString\", FormalParameters(FormalParameter(Modifier(), Type(QualifiedIdentifier(\"C\"), null), null, \"__this\", null)), null, null, Block(ReturnStatement(StringLiteral(\"\\\"still C\\\"\"))))))"));
        assertTrue("Test012", l.get(0).get(5).toString().equals("ClassDeclaration(Modifiers(null), \"Test012\", null, null, null, ClassBody(MethodDeclaration(Modifiers(), null, \"int\", \"main\", Arguments(VoidType()), null, null, Block(FieldDeclaration(Modifiers(), Type(QualifiedIdentifier(\"A\"), null), Declarators(Declarator(\"a\", \"(A) \", Expression(\"__A::__init\", Arguments(NewClassExpression(null, null, QualifiedIdentifier(\"__A\"), Arguments(), null)))))), ExpressionStatement(CallExpression(PrimaryIdentifier(\"a\"), null, \"-> __vptr -> setA\", Arguments(PrimaryIdentifier(\"a\"), StringLiteral(\"\\\"A\\\"\")))), FieldDeclaration(Modifiers(), Type(QualifiedIdentifier(\"B1\"), null), Declarators(Declarator(\"b1\", \"(B1) \", Expression(\"__B1::__init\", Arguments(NewClassExpression(null, null, QualifiedIdentifier(\"__B1\"), Arguments(), null)))))), ExpressionStatement(CallExpression(PrimaryIdentifier(\"b1\"), null, \"-> __vptr -> setA\", Arguments(PrimaryIdentifier(\"b1\"), StringLiteral(\"\\\"B1\\\"\")))), FieldDeclaration(Modifiers(), Type(QualifiedIdentifier(\"B2\"), null), Declarators(Declarator(\"b2\", \"(B2) \", Expression(\"__B2::__init\", Arguments(NewClassExpression(null, null, QualifiedIdentifier(\"__B2\"), Arguments(), null)))))), ExpressionStatement(CallExpression(PrimaryIdentifier(\"b2\"), null, \"-> __vptr -> setA\", Arguments(PrimaryIdentifier(\"b2\"), StringLiteral(\"\\\"B2\\\"\")))), FieldDeclaration(Modifiers(), Type(QualifiedIdentifier(\"C\"), null), Declarators(Declarator(\"c\", \"(C) \", Expression(\"__C::__init\", Arguments(NewClassExpression(null, null, QualifiedIdentifier(\"__C\"), Arguments(), null)))))), ExpressionStatement(CallExpression(PrimaryIdentifier(\"c\"), null, \"-> __vptr -> setA\", Arguments(PrimaryIdentifier(\"c\"), StringLiteral(\"\\\"C\\\"\")))), ExpressionStatement(CallExpression(PrimaryIdentifier(\"a\"), null, \"-> __vptr -> printOther\", Arguments(PrimaryIdentifier(\"a\")))), ExpressionStatement(CallExpression(PrimaryIdentifier(\"a\"), null, \"-> __vptr -> printOther\", Arguments(PrimaryIdentifier(\"a\"), PrimaryIdentifier(\"b1\")))), ExpressionStatement(CallExpression(PrimaryIdentifier(\"a\"), null, \"-> __vptr -> printOther\", Arguments(PrimaryIdentifier(\"a\"), PrimaryIdentifier(\"b2\")))), ExpressionStatement(CallExpression(PrimaryIdentifier(\"a\"), null, \"-> __vptr -> printOther\", Arguments(PrimaryIdentifier(\"a\"), PrimaryIdentifier(\"c\")))), ReturnStatement(\"0\")))))"));
    }

}