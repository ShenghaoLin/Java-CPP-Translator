#pragma once

#include <stdint.h>
#include <string>

// ==========================================================================

// To avoid the "static initialization order fiasco", we use functions
// instead of fields/variables for all pointer values that are statically
// initialized.

// See https://isocpp.org/wiki/faq/ctors#static-init-order

// ==========================================================================

namespace java
{
namespace lang
{

// Forward declarations of data layout and vtables.
// This lets the compiler know that we have definitions forthcoming later in the program.
// See http://www.learncpp.com/cpp-tutorial/17-forward-declarations/
struct __Object;
struct __Object_VT;

struct __String;
struct __String_VT;

struct __Class;
struct __Class_VT;

// Definition of types that are equivalent to Java semantics,
// i.e., an instance is the address of the object's data layout.
typedef __Object* Object;
typedef __Class* Class;
typedef __String* String;

// ======================================================================

// The data layout for java.lang.Object.
// Think of this as roughly the 'properties' of java.lang.Object.
struct __Object
{
    // A pointer to a vtable which could differ at runtime from the static
    // reference we have to the Object vtable below. main.cc will demonstrate this.
    __Object_VT* __vptr;

    // The constructor.
    __Object();

    // The methods implemented by java.lang.Object.
    static int32_t hashCode(Object);
    static bool equals(Object, Object);
    static Class getClass(Object);
    static String toString(Object);

    // The function returning the class object representing java.lang.Object.
    static Class __class();

    // The vtable for java.lang.Object itself.
    // Moreover, always a reference to the behaviours of java.lang.Object.
    static __Object_VT __vtable;
};

// The vtable layout for java.lang.Object.
// Think of this as roughly the 'methods' of java.lang.Object.
struct __Object_VT
{
    // The dynamic type, main.cc will demonstrate this.
    Class __is_a;

    // These properties are function pointers, the syntax:
    // ex:   int32_t     (*sum)          (int32_t, int32_t);
    //       return_type (*function_name)(arg_type_list);
    // See http://www.learncpp.com/cpp-tutorial/78-function-pointers/
    int32_t (*hashCode)(Object);
    bool (*equals)(Object, Object);
    Class (*getClass)(Object);
    String (*toString)(Object);

    // The vtable constructor. Notice that it is initializing
    // the function pointer properties with references to the
    // static implementations provided by Object itself.
    // This is how "subclasses" will "inherit" from "superclasses"
    __Object_VT()
        : __is_a(__Object::__class()),
          hashCode(&__Object::hashCode),
          equals(&__Object::equals),
          getClass(&__Object::getClass),
          toString(&__Object::toString)
    {
    }
};

// ======================================================================

// The data layout for java.lang.String.
struct __String
{
    __String_VT* __vptr;

    // The member that contains the actual string data.
    std::string data;

    // The constructor
    __String(std::string data);

    // The methods implemented by java.lang.String.
    static int32_t hashCode(String);
    static bool equals(String, Object);
    static String toString(String);
    static int32_t length(String);
    static char charAt(String, int32_t);

    // The function returning the class object representing java.lang.String.
    static Class __class();

    // The vtable for java.lang.String.
    static __String_VT __vtable;
};

// The vtable layout for java.lang.String.
struct __String_VT
{
    // The dynamic type.
    Class __is_a;

    int32_t (*hashCode)(String);
    bool (*equals)(String, Object);
    Class (*getClass)(String);
    String (*toString)(String);
    int32_t (*length)(String);
    char (*charAt)(String, int32_t);

    __String_VT()
        : __is_a(__String::__class()),
          hashCode(&__String::hashCode),
          equals(&__String::equals),
          getClass((Class(*)(String)) &__Object::getClass), // "inheriting" getClass from Object
          toString(&__String::toString),
          length(&__String::length),
          charAt(&__String::charAt)
    {
    }
};

// ======================================================================

// Class is a little special in that all other classes will be 'composed' with
// a Class instance. Its purpose is to encapsulate type information about a runtime 'instance'.
// See http://docs.oracle.com/javase/7/docs/api/java/lang/Class.html

// The data layout for java.lang.Class.
struct __Class
{
    __Class_VT* __vptr;
    String name;
    Class parent;

    // The constructor.
    __Class(String name, Class parent);

    // The instance methods of java.lang.Class.
    static String toString(Class);
    static String getName(Class);
    static Class getSuperclass(Class);
    static bool isInstance(Class, Object);

    // The function returning the class object representing java.lang.Class.
    static Class __class();

    // The vtable for java.lang.Class.
    static __Class_VT __vtable;
};

// The vtable layout for java.lang.Class.
struct __Class_VT
{
    // The dynamic type.
    Class __is_a;

    int32_t (*hashCode)(Class);
    bool (*equals)(Class, Object);
    Class (*getClass)(Class);
    String (*toString)(Class);
    String (*getName)(Class);
    Class (*getSuperclass)(Class);
    bool (*isInstance)(Class, Object);

    __Class_VT()
        : __is_a(__Class::__class()),
          hashCode((int32_t(*)(Class)) &__Object::hashCode),
          equals((bool(*)(Class,Object)) &__Object::equals),
          getClass((Class(*)(Class)) &__Object::getClass),
          toString(&__Class::toString),
          getName(&__Class::getName),
          getSuperclass(&__Class::getSuperclass),
          isInstance(&__Class::isInstance)
    {
    }
};

}
}

// ==========================================================================

namespace __rt
{

// The function returning the canonical null value.
java::lang::Object null();

// Function for converting a C string literal to a translated
// Java string.
inline java::lang::String literal(const char * s)
{
    // C++ implicitly converts the C string to a std::string.
    return new java::lang::__String(s);
}

}
