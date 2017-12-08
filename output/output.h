#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace test012 { 

  struct __A;
  struct __A_VT;

  struct __B1;
  struct __B1_VT;

  struct __B2;
  struct __B2_VT;

  struct __C;
  struct __C_VT;

  struct __Test012;
  struct __Test012_VT;

  typedef __rt::Ptr<__A> A;
  typedef __rt::Ptr<__B1> B1;
  typedef __rt::Ptr<__B2> B2;
  typedef __rt::Ptr<__C> C;
  typedef __rt::Ptr<__Test012> Test012;

  struct __A {

    __A_VT* __vptr;
    String a;

    __A();
    static A __init(A __this);

    static void setA_String_0(A __this, __rt::Array<String> x);
    static void printOther_A_0(A __this, A other);
    static String myToString_0(A __this);

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
    void (*setA_String_0)(A,__rt::Array<String>);
    void (*printOther_A_0)(A,A);
    String (*myToString_0)(A);

    __A_VT()
      :__is_a(__A::__class()),
      __delete(&__rt::__delete<__A>),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString((String(*)(A)) &__Object::toString),
      setA_String_0(&__A::setA_String_0),
      printOther_A_0(&__A::printOther_A_0),
      myToString_0(&__A::myToString_0)
    {
    }
  };

  struct __B1 {

    __B1_VT* __vptr;
    String a;
    String b;

    __B1();
    static B1 __init(B1 __this);


    static Class __class();

    static __B1_VT __vtable;
  };

  struct __B1_VT {

    Class __is_a;

    void (*__delete)(__B1*);
    int32_t (*hashCode)(B1);
    bool (*equals)(B1, Object);
    Class (*getClass)(B1);
    String (*toString)(B1);
    void (*setA_String_0)(B1,__rt::Array<String>);
    void (*printOther_A_0)(B1,A);
    String (*myToString_0)(B1);

    __B1_VT()
      :__is_a(__B1::__class()),
      __delete(&__rt::__delete<__B1>),
      hashCode((int32_t(*)(B1)) &__Object::hashCode),
      equals((bool(*)(B1, Object)) &__Object::equals),
      getClass((Class(*)(B1)) &__Object::getClass),
      toString((String(*)(B1)) &__Object::toString),
      setA_String_0((void(*)(B1,__rt::Array<String>)) &__A::setA_String_0),
      printOther_A_0((void(*)(B1,A)) &__A::printOther_A_0),
      myToString_0((String(*)(B1)) &__A::myToString_0)
    {
    }
  };

  struct __B2 {

    __B2_VT* __vptr;
    String a;
    String b;

    __B2();
    static B2 __init(B2 __this);


    static Class __class();

    static __B2_VT __vtable;
  };

  struct __B2_VT {

    Class __is_a;

    void (*__delete)(__B2*);
    int32_t (*hashCode)(B2);
    bool (*equals)(B2, Object);
    Class (*getClass)(B2);
    String (*toString)(B2);
    void (*setA_String_0)(B2,__rt::Array<String>);
    void (*printOther_A_0)(B2,A);
    String (*myToString_0)(B2);

    __B2_VT()
      :__is_a(__B2::__class()),
      __delete(&__rt::__delete<__B2>),
      hashCode((int32_t(*)(B2)) &__Object::hashCode),
      equals((bool(*)(B2, Object)) &__Object::equals),
      getClass((Class(*)(B2)) &__Object::getClass),
      toString((String(*)(B2)) &__Object::toString),
      setA_String_0((void(*)(B2,__rt::Array<String>)) &__A::setA_String_0),
      printOther_A_0((void(*)(B2,A)) &__A::printOther_A_0),
      myToString_0((String(*)(B2)) &__A::myToString_0)
    {
    }
  };

  struct __C {

    __C_VT* __vptr;
    String a;
    String b;
    String c;

    __C();
    static C __init(C __this);

    static String myToString1(C __this);

    static Class __class();

    static __C_VT __vtable;
  };

  struct __C_VT {

    Class __is_a;

    void (*__delete)(__C*);
    int32_t (*hashCode)(C);
    bool (*equals)(C, Object);
    Class (*getClass)(C);
    String (*toString)(C);
    void (*setA_String_0)(C,__rt::Array<String>);
    void (*printOther_A_0)(C,A);
    String (*myToString_0)(C);
    String (*myToString1)(C);

    __C_VT()
      :__is_a(__C::__class()),
      __delete(&__rt::__delete<__C>),
      hashCode((int32_t(*)(C)) &__Object::hashCode),
      equals((bool(*)(C, Object)) &__Object::equals),
      getClass((Class(*)(C)) &__Object::getClass),
      toString((String(*)(C)) &__Object::toString),
      setA_String_0((void(*)(C,__rt::Array<String>)) &__A::setA_String_0),
      printOther_A_0((void(*)(C,A)) &__A::printOther_A_0),
      myToString_0((String(*)(C)) &__A::myToString_0),
      myToString1(&__C::myToString1)
    {
    }
  };

  struct __Test012 {

    __Test012_VT* __vptr;

    __Test012();
    static Test012 __init(Test012 __this);

    static int main(__rt::Array<String> args);

    static Class __class();

    static __Test012_VT __vtable;
  };

  struct __Test012_VT {

    Class __is_a;

    void (*__delete)(__Test012*);
    int32_t (*hashCode)(Test012);
    bool (*equals)(Test012, Object);
    Class (*getClass)(Test012);
    String (*toString)(Test012);

    __Test012_VT()
      :__is_a(__Test012::__class()),
      __delete(&__rt::__delete<__Test012>),
      hashCode((int32_t(*)(Test012)) &__Object::hashCode),
      equals((bool(*)(Test012, Object)) &__Object::equals),
      getClass((Class(*)(Test012)) &__Object::getClass),
      toString((String(*)(Test012)) &__Object::toString)
    {
    }
  };

}
}
