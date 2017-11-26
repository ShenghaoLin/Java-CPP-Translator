/**
 * Method object that holds information a method declaration would have
 * these are: accessModifier, isStatic, returnType, name, and parameters
 * no methods are implemented, only a constructor that initialzes the object
 *
 * @author Goktug Saatcioglu
 * @author Shenghao Lin
 *
 * @version 1.0
 */

package edu.nyu.oop;

import java.util.ArrayList;

public class Method {

    public String accessModifier;
    public boolean isStatic;
    public String returnType;
    public String name;
    public ArrayList<Parameter> parameters;

    public Method(String accessModifier, boolean isStatic, String returnType, String name, ArrayList<Parameter> parameters) {
        this.accessModifier = accessModifier;
        this.isStatic = isStatic;
        this.returnType = returnType;
        this.name = name;
        this.parameters = parameters;
    }
}