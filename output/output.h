#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace javalang3 { 

  struct __A;
  struct __A_VT;

  struct __B;
  struct __B_VT;

  struct __Main;
  struct __Main_VT;

  typedef __rt::Ptr<__A> A;
  typedef __rt::Ptr<__B> B;
  typedef __rt::Ptr<__Main> Main;

  struct __A {

    __A_VT* __vptr;
    char x;
    char y;

    __A();
    static A __init(A __this, char x, char y);
    static A __init(A __this, char x);

    static String toString(A __this);
    static void overloaded_int_0(A __this, int32_t i);
    static void overloaded_byte_0(A __this, int8_t b);
    static A overloaded_A_0(A __this, A a);
    static void print_0(A __this);

    static Class __class();

    static __A_VT __vtable;
  };

  struct __A_VT {

    Class __is_a;

    void (*__delete)(__A*);
    int32_t (*hashCode)(A);
    bool (*equals)(A, Object);
    Class (*getClass)(A);
    String (*toString)(A);
    void (*overloaded_int_0)(A,int32_t);
    void (*overloaded_byte_0)(A,int8_t);
    A (*overloaded_A_0)(A,A);

    __A_VT()
      __is_a(__A::__class()),
      __delete(&__rt::__delete<__A>),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString(&__A::toString),
      overloaded_int_0(&__A::overloaded_int_0),
      overloaded_byte_0(&__A::overloaded_byte_0),
      overloaded_A_0(&__A::overloaded_A_0)
    {
    }
  };

  struct __B {

    __B_VT* __vptr;
    char x;
    char y;
    char z;
    String s;

    __B();
    static B __init(B __this);
    static B __init(B __this, char z);

    static String toString(B __this);
    static A overloaded_B_0(B __this, B b);

    static Class __class();

    static __B_VT __vtable;
  };

  struct __B_VT {

    Class __is_a;

    void (*__delete)(__B*);
    int32_t (*hashCode)(B);
    bool (*equals)(B, Object);
    Class (*getClass)(B);
    String (*toString)(B);
    void (*overloaded_int_0)(B,int32_t);
    void (*overloaded_byte_0)(B,int8_t);
    A (*overloaded_A_0)(B,A);
    A (*overloaded_B_0)(B,B);

    __B_VT()
      __is_a(__B::__class()),
      __delete(&__rt::__delete<__B>),
      hashCode((int32_t(*)(B)) &__Object::hashCode),
      equals((bool(*)(B, Object)) &__Object::equals),
      getClass((Class(*)(B)) &__Object::getClass),
      toString(&__B::toString),
      overloaded_int_0((void(*)(B,int32_t)) &__A::overloaded_int_0),
      overloaded_byte_0((void(*)(B,int8_t)) &__A::overloaded_byte_0),
      overloaded_A_0((A(*)(B,A)) &__A::overloaded_A_0),
      overloaded_B_0(&__B::overloaded_B_0)
    {
    }
  };

  struct __Main {

    __Main_VT* __vptr;

    __Main();
    static Main __init(Main __this);

    static void main(__rt::Array<String> args);

    static Class __class();

    static __Main_VT __vtable;
  };

  struct __Main_VT {

    Class __is_a;

    void (*__delete)(__Main*);
    int32_t (*hashCode)(Main);
    bool (*equals)(Main, Object);
    Class (*getClass)(Main);
    String (*toString)(Main);

    __Main_VT()
      __is_a(__Main::__class()),
      __delete(&__rt::__delete<__Main>),
      hashCode((int32_t(*)(Main)) &__Object::hashCode),
      equals((bool(*)(Main, Object)) &__Object::equals),
      getClass((Class(*)(Main)) &__Object::getClass),
      toString((String(*)(Main)) &__Object::toString)
    {
    }
  };

}
}
