/**
 * Field object that holds information a field declaration would have
 * these are: accessModifier, isStatic, fieldType, fieldName, and intial
 * no methods are implemented, only a constructor that initialzes the object
 * furthermore there is a "hidden" field inheritedFrom which is used in
 * determinig where a vtable field initially came from in Phase 2 logic
 *
 * @author Goktug Saatcioglu
 * @author Shenghao Lin
 *
 * @version 1.0
 */

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