/**
 * Constructor object that holds information a construcor declaration would have
 * these are: accessModifier, name, and parameters
 * no methods are implemented, only a constructor that initialzes the object
 *
 * @author Goktug Saatcioglu
 * @author Shenghao Lin
 *
 * @version 1.0
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
}