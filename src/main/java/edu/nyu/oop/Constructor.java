/**
 * Constructor object that holds information a construcor declaration would have
 * these are: accessModifier, name, and parameters
 * no methods are implemented, only a constructor that initialzes the object
 *
 * @author Goktug Saatcioglu
 * @author Shenghao Lin
 *
 * @version 2.0
 */

package edu.nyu.oop;

import java.util.ArrayList;

public class Constructor {

    public String accessModifier;
    public String name;
    public ArrayList<Parameter> parameters;

    public Constructor(String accessModifier, String name, ArrayList<Parameter> parameters) {
        this.accessModifier = accessModifier;
        this.name = name;
        this.parameters = parameters;
    }

    /**
     * equals method to see if two constructors are the same
     *
     * @param  Constructor takes in another constructor for the comparison
     * @return true if two constructors are the same, false otherwise
     */
    public boolean equals(Constructor other) {
        if (this.accessModifier.equals(other.accessModifier) && this.name.equals(other.name)) {
            if (this.parameters == null && other.parameters == null) return true;
            else if (this.parameters.size() == other.parameters.size()) {
                for (int i = 0; i < this.parameters.size(); i++) {
                    if (!this.parameters.get(i).equals(other.parameters.get(i))) return false;
                }
                return true;
            }
            return false;
        }
        return false;
    }
}