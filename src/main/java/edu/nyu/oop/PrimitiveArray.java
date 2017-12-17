/**
 * Container class for primitive arrays that takes in a
 * primitive java type and gives back the correct template
 * declaration of that primitive array
 *
 * @author Goktug Saatcioglu
 *
 * @version 2.0
 */


package edu.nyu.oop;

import java.lang.StringBuilder;

public class PrimitiveArray {

    public String javaType;
    public String cppType;

    public PrimitiveArray(String javaType) {
        this.javaType = javaType;
        this.cppType = convertToCppType(javaType);
    }

    public String dump() {
        String startDeclaration = "template<>\njava::lang::Class __rt::__Array<";
        StringBuilder temp = new StringBuilder(startDeclaration);
        temp.append(cppType);
        temp.append(">::__class()\n");
        temp.append("{\n");
        temp.append("  static java::lang::Class tk =\n");
        temp.append("\tnew java::lang::__Class(__rt::literal(\"");
        temp.append(javaType);
        temp.append("\"),\n");
        temp.append("\t\t\t\t\t\t\t(java::lang::Class) __rt::null(),\n");
        temp.append("\t\t\t\t\t\t\t(java::lang::Class) __rt::null(),\n");
        temp.append("\t\t\t\t\t\t\ttrue);\n");
        temp.append("  static java::lang::Class k =\n");
        temp.append("\tnew java::lang::__Class(__rt::literal(\"[");
        temp.append(convertToBigType(javaType));
        temp.append("\"),\n");
        temp.append("\t\t\t\t\t\t\tjava::lang::__Object::__class(),\n");
        temp.append("\t\t\t\t\t\t\ttk);\n");
        temp.append("return k;\n");
        temp.append("}\n");
        return temp.toString();

    }

    public String convertToCppType(String javaType) {
        String cppType;
        switch (javaType) {
            case "long":
                cppType = "int64_t";
                break;
            case "int":
                cppType = "int32_t";
                break;
            case "short":
                cppType = "int16_t";
                break;
            case "byte":
                cppType = "int8_t";
                break;
            case "boolean":
                cppType = "bool";
                break;
            default:
                cppType = javaType;
                break;
        }
        return cppType;
    }

    public String convertToBigType(String javaType) {
        String bigType;
        switch (javaType) {
            case "double":
                bigType = "D";
                break;
            case "float":
                bigType = "F";
                break;
            case "long":
                bigType = "J";
                break;
            case "int":
                bigType = "I";
                break;
            case "short":
                bigType = "S";
                break;
            case "char":
                bigType = "C";
                break;
            case "byte":
                bigType = "B";
                break;
            case "boolean":
                bigType = "Z";
                break;
            default:
                bigType = "";
                break;
        }
        return bigType;
    }
}