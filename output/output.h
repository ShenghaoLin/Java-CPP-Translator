#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace javalang { 

  struct __A;
  struct __A_VT;

  struct __B;
  struct __B_VT;

  typedef __rt::Ptr<__A> A;
  typedef __rt::Ptr<__B> B;

  struct __A {

    __A_VT* __vptr;
    char x;
    char y;

    __A();
    static A __init(A __this);

    static Placeholder A_0(A __this, char x, char y);
    static Placeholder A_1(A __this, char x);
    static String toString_0(A __this);
    static void overloaded_0(A __this, int32_t i);
    static void overloaded_1(A __this, int8_t b);
    static A overloaded_2(A __this, A a);
    static void print_0(A __this);

    static Class __class();

    static __A_VT __vtable;
  };

  struct __A_VT {

    Class __is_a;

    int32_t (*hashCode)(A);
    bool (*equals)(A, Object);
    Class (*getClass)(A);
    String (*toString)(A);
    Placeholder (*A_0)(A,char,char);
    Placeholder (*A_1)(A,char);
    String (*toString_0)(A);
    void (*overloaded_0)(A,int32_t);
    void (*overloaded_1)(A,int8_t);
    A (*overloaded_2)(A,A);

    __A_VT()
      __is_a(__A::__class()),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString((String(*)(A)) &__Object::toString),
      A_0(&__A::A_0),
      A_1(&__A::A_1),
      toString_0(&__A::toString_0),
      overloaded_0(&__A::overloaded_0),
      overloaded_1(&__A::overloaded_1),
      overloaded_2(&__A::overloaded_2)
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

    static String toString_0(B __this);
    static A overloaded_0(B __this, B b);
    static Placeholder B_0(B __this);
    static Placeholder B_1(B __this, char z);

    static Class __class();

    static __B_VT __vtable;
  };

  struct __B_VT {

    Class __is_a;

    int32_t (*hashCode)(B);
    bool (*equals)(B, Object);
    Class (*getClass)(B);
    String (*toString)(B);
    Placeholder (*A_0)(B,char,char);
    Placeholder (*A_1)(B,char);
    String (*toString_0)(B);
    A (*overloaded_0)(B,B);
    void (*overloaded_1)(B,int8_t);
    A (*overloaded_2)(B,A);
    Placeholder (*B_0)(B);
    Placeholder (*B_1)(B,char);

    __B_VT()
      __is_a(__B::__class()),
      hashCode((int32_t(*)(B)) &__Object::hashCode),
      equals((bool(*)(B, Object)) &__Object::equals),
      getClass((Class(*)(B)) &__Object::getClass),
      toString((String(*)(B)) &__Object::toString),
      A_0((Placeholder(*)(B,char,char)) &__A::A_0),
      A_1((Placeholder(*)(B,char)) &__A::A_1),
      toString_0(&__B::toString_0),
      overloaded_0(&__B::overloaded_0),
      overloaded_1((void(*)(B,int8_t)) &__A::overloaded_1),
      overloaded_2((A(*)(B,A)) &__A::overloaded_2),
      B_0(&__B::B_0),
      B_1(&__B::B_1)
    {
    }
  };

}
}
#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace javalang { 

    struct __A;
    struct __A_VT;

    struct __B;
    struct __B_VT;

    typedef __rt::Ptr<__A> A;
    typedef __rt::Ptr<__B> B;

    struct __A {

      __A_VT* __vptr;

      __A();
      static A __init(A __this);

      static int32_t method_0(A __this);
      static String toString_0(A __this);

      static Class __class();

      static __A_VT __vtable;
    };

    struct __A_VT {

      Class __is_a;

      int32_t (*hashCode)(A);
      bool (*equals)(A, Object);
      Class (*getClass)(A);
      String (*toString)(A);
      int32_t (*method_0)(A);
      String (*toString_0)(A);

      __A_VT()
        __is_a(__A::__class()),
        hashCode((int32_t(*)(A)) &__Object::hashCode),
        equals((bool(*)(A, Object)) &__Object::equals),
        getClass((Class(*)(A)) &__Object::getClass),
        toString((String(*)(A)) &__Object::toString),
        method_0(&__A::method_0),
        toString_0(&__A::toString_0)
      {
      }
    };

    struct __B {

      __B_VT* __vptr;

      __B();
      static B __init(B __this);

      static String toString_0(B __this);

      static Class __class();

      static __B_VT __vtable;
    };

    struct __B_VT {

      Class __is_a;

      int32_t (*hashCode)(B);
      bool (*equals)(B, Object);
      Class (*getClass)(B);
      String (*toString)(B);
      int32_t (*method_0)(B);
      String (*toString_0)(B);

      __B_VT()
        __is_a(__B::__class()),
        hashCode((int32_t(*)(B)) &__Object::hashCode),
        equals((bool(*)(B, Object)) &__Object::equals),
        getClass((Class(*)(B)) &__Object::getClass),
        toString((String(*)(B)) &__Object::toString),
        method_0((int32_t(*)(B)) &__A::method_0),
        toString_0(&__B::toString_0)
      {
      }
    };

}
}
