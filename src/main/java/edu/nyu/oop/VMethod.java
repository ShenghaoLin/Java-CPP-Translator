/**
 * VMethod object that holds information a vmethod declaration would have
 * these are: accessModifier, isStatic, returnType, name, and intial
 * a vmethod is slightly different than a method and thus gets it own
 * representation
 * no methods are implemented, only a constructor that initialzes the object
 *
 * @author Sam Holloway
 * @author Goktug Saatcioglu
 *
 * @version 1.0
 */

package edu.nyu.oop;

import java.util.ArrayList;

public class VMethod {

    public String accessModifier;
    public boolean isStatic;
    public String name;
    public String initial;

    public VMethod(String accessModifier, boolean isStatic, String name, String initial) {
        this.accessModifier = accessModifier;
        this.isStatic = isStatic;
        this.name = name;
        this.initial = initial;
    }
}