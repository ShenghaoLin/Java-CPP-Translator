/**
 * Parameter object that holds information a method declaration would have
 * these are: type and name
 * no methods are implemented, only a constructor that initialzes the object
 *
 * @author Goktug Saatcioglu
 * @author Shenghao Lin
 *
 * @version 1.0
 */

package edu.nyu.oop;

public class Parameter {
	
	public String type;
	public String name;

	public Parameter(String type, String name) {
		this.type = type;
		this.name = name;
	}
}