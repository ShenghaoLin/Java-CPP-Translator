/**
 * Necessary template declarations for non-primitive types
 * no longer in use, will be removed in future implementations
 *
 * @author Goktug Saatcioglu
 *
 * @version 2.0
 *
 * @Deprecated
 */


package edu.nyu.oop;

import java.lang.StringBuilder;

public class BigArray {

    public String className;
    public String packageName;

    public BigArray(String className, String packageName) {
        this.className = className;
        this.packageName = packageName.replace(".", "::") + "::" + className;
    }

    public String dump() {
        String startDeclaration = "template<>\njava::lang::Class __Array<";
        StringBuilder temp = new StringBuilder(startDeclaration);
        temp.append(packageName);
        temp.append("::__class()\n");
        temp.append("{\n");
        temp.append("  static java::lang::Class k =\n");
        temp.append("\tnew java::lang::__Class(literal(\"[L");
        temp.append(packageName.replace("::", "."));
        temp.append(";\"),\n");
        temp.append("\t\t\t\t\t\t\tjava::lang::__Object::__class(),\n");
        temp.append("\t\t\t\t\t\t\t");
        temp.append(packageName.replace(className, "__" + className));
        temp.append("::__class());\n");
        temp.append("return k;\n");
        temp.append("}\n");
        return temp.toString();
    }
}