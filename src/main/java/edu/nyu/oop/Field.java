package edu.nyu.oop;

public class Field {

	public String accessModifier;
	public boolean isStatic;
	public String fieldType;
	public String fieldName;
	public String initial;
	public String inheritedFrom;

	public Field(String accessModifier, boolean isStatic, String fieldType, String fieldName, String initial) {
		this.accessModifier = accessModifier;
		this.isStatic = isStatic;
		this.fieldType = fieldType;
		this.fieldName = fieldName;
		this.initial = initial;
		this.inheritedFrom = "";
	}
}