#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace test011 { 

  struct __A;
  struct __A_VT;

  struct __B1;
  struct __B1_VT;

  struct __B2;
  struct __B2_VT;

  struct __C;
  struct __C_VT;

  struct __Test011;
  struct __Test011_VT;

  typedef __rt::Ptr<__A> A;
  typedef __rt::Ptr<__B1> B1;
  typedef __rt::Ptr<__B2> B2;
  typedef __rt::Ptr<__C> C;
  typedef __rt::Ptr<__Test011> Test011;

  struct __A {

    __A_VT* __vptr;
    String a;

    __A();
    static A __init(A __this);

    static String toString(A __this);
    static void setA_String_(A __this, String x);
    static void printOther_A_(A __this, A other);

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
    void (*setA_String_)(A,String);
    void (*printOther_A_)(A,A);

    __A_VT()
      :__is_a(__A::__class()),
      __delete(&__rt::__delete<__A>),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString(&__A::toString),
      setA_String_(&__A::setA_String_),
      printOther_A_(&__A::printOther_A_)
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
    void (*setA_String_)(B1,String);
    void (*printOther_A_)(B1,A);

    __B1_VT()
      :__is_a(__B1::__class()),
      __delete(&__rt::__delete<__B1>),
      hashCode((int32_t(*)(B1)) &__Object::hashCode),
      equals((bool(*)(B1, Object)) &__Object::equals),
      getClass((Class(*)(B1)) &__Object::getClass),
      toString((String(*)(B1)) &__A::toString),
      setA_String_((void(*)(B1,String)) &__A::setA_String_),
      printOther_A_((void(*)(B1,A)) &__A::printOther_A_)
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
    void (*setA_String_)(B2,String);
    void (*printOther_A_)(B2,A);

    __B2_VT()
      :__is_a(__B2::__class()),
      __delete(&__rt::__delete<__B2>),
      hashCode((int32_t(*)(B2)) &__Object::hashCode),
      equals((bool(*)(B2, Object)) &__Object::equals),
      getClass((Class(*)(B2)) &__Object::getClass),
      toString((String(*)(B2)) &__A::toString),
      setA_String_((void(*)(B2,String)) &__A::setA_String_),
      printOther_A_((void(*)(B2,A)) &__A::printOther_A_)
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
    void (*setA_String_)(C,String);
    void (*printOther_A_)(C,A);

    __C_VT()
      :__is_a(__C::__class()),
      __delete(&__rt::__delete<__C>),
      hashCode((int32_t(*)(C)) &__Object::hashCode),
      equals((bool(*)(C, Object)) &__Object::equals),
      getClass((Class(*)(C)) &__Object::getClass),
      toString((String(*)(C)) &__A::toString),
      setA_String_((void(*)(C,String)) &__A::setA_String_),
      printOther_A_((void(*)(C,A)) &__A::printOther_A_)
    {
    }
  };

  struct __Test011 {

    __Test011_VT* __vptr;

    __Test011();
    static Test011 __init(Test011 __this);

    static int32_t main(__rt::Array<String> args);

    static Class __class();

    static __Test011_VT __vtable;
  };

  struct __Test011_VT {

    Class __is_a;

    void (*__delete)(__Test011*);
    int32_t (*hashCode)(Test011);
    bool (*equals)(Test011, Object);
    Class (*getClass)(Test011);
    String (*toString)(Test011);

    __Test011_VT()
      :__is_a(__Test011::__class()),
      __delete(&__rt::__delete<__Test011>),
      hashCode((int32_t(*)(Test011)) &__Object::hashCode),
      equals((bool(*)(Test011, Object)) &__Object::equals),
      getClass((Class(*)(Test011)) &__Object::getClass),
      toString((String(*)(Test011)) &__Object::toString)
    {
    }
  };

}
}
