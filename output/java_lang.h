/*
 * Object-Oriented Programming
 * Copyright (C) 2012 Robert Grimm
 * Modifications Copyright (C) 2013 Thomas Wies
 * Modifications Copyright (C) 2015 Randy Shepherd
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301,
 * USA.
 */

#pragma once

#include <stdint.h>
#include <string>

// ==========================================================================

// To avoid the "static initialization order fiasco", we use functions
// instead of fields/variables for all pointer values that are statically
// initialized.

// See http://www.parashift.com/c++-faq-lite/ctors.html#faq-10.14.

// ==========================================================================

namespace java
{
namespace lang
{

// Forward declarations of data layout and vtables.
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
struct __Object
{
  __Object_VT* __vptr;

  // The constructor.
  __Object();

  // The methods implemented by java.lang.Object.
  static int32_t hashCode(Object);
  static bool equals(Object, Object);
  static Class getClass(Object);
  static String toString(Object);

  // The function returning the class object representing
  // java.lang.Object.
  static Class __class();

  // The vtable for java.lang.Object.
  static __Object_VT __vtable;
};

// The vtable layout for java.lang.Object.
struct __Object_VT
{
  Class __isa;
  int32_t (*hashCode)(Object);
  bool (*equals)(Object, Object);
  Class (*getClass)(Object);
  String (*toString)(Object);

  __Object_VT()
    : __isa(__Object::__class()),
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
  std::string data;

  // The constructor;
  __String(std::string data);

  // The methods implemented by java.lang.String.
  static int32_t hashCode(String);
  static bool equals(String, Object);
  static String toString(String);
  static int32_t length(String);
  static char charAt(String, int32_t);

  // The function returning the class object representing
  // java.lang.String.
  static Class __class();

  // The vtable for java.lang.String.
  static __String_VT __vtable;
};

// The vtable layout for java.lang.String.
struct __String_VT
{
  Class __isa;
  int32_t (*hashCode)(String);
  bool (*equals)(String, Object);
  Class (*getClass)(String);
  String (*toString)(String);
  int32_t (*length)(String);
  char (*charAt)(String, int32_t);

  __String_VT()
    : __isa(__String::__class()),
      hashCode(&__String::hashCode),
      equals(&__String::equals),
      getClass((Class(*)(String)) &__Object::getClass),
      toString(&__String::toString),
      length(&__String::length),
      charAt(&__String::charAt)
  {
  }
};

// ======================================================================

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

  // The function returning the class object representing
  // java.lang.Class.
  static Class __class();

  // The vtable for java.lang.Class.
  static __Class_VT __vtable;
};

// The vtable layout for java.lang.Class.
struct __Class_VT
{
  Class __isa;
  int32_t (*hashCode)(Class);
  bool (*equals)(Class, Object);
  Class (*getClass)(Class);
  String (*toString)(Class);
  String (*getName)(Class);
  Class (*getSuperclass)(Class);
  bool (*isInstance)(Class, Object);

  __Class_VT()
    : __isa(__Class::__class()),
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

// Function for converting a C string lieral to a translated
// Java string.
inline java::lang::String literal(const char * s)
{
  // C++ implicitly converts the C string to a std::string.
  return new java::lang::__String(s);
}

}
