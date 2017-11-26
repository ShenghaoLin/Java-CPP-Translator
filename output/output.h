#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace test026 { 

  struct __A;
  struct __A_VT;

  struct __B;
  struct __B_VT;

  typedef __rt::Ptr<__A> A;
  typedef __rt::Ptr<__B> B;

  struct __A {

    __A_VT* __vptr;
    int32_t i;

    __A();
    static A __init(A __this);

    static Placeholder A(A __this, int32_t i);
    static int32_t get(A __this);

    static Class __class();

    static __A_VT __vtable;
  };

  struct __A_VT {

    Class __is_a;

    int32_t (*hashCode)(A);
    bool (*equals)(A, Object);
    Class (*getClass)(A);
    String (*toString)(A);
    Placeholder (*A)(A,int32_t);
    int32_t (*get)(A);

    __A_VT()
      __is_a(__A::__class()),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString((String(*)(A)) &__Object::toString),
      A(&__A::A),
      get(&__A::get)
    {
    }
  };

  struct __B {

    __B_VT* __vptr;
    int32_t i;

    __B();
    static B __init(B __this);

    static int32_t get(B __this);
    static Placeholder B(B __this, int32_t i);

    static Class __class();

    static __B_VT __vtable;
  };

  struct __B_VT {

    Class __is_a;

    int32_t (*hashCode)(B);
    bool (*equals)(B, Object);
    Class (*getClass)(B);
    String (*toString)(B);
    Placeholder (*A)(B,int32_t);
    int32_t (*get)(B);
    Placeholder (*B)(B,int32_t);

    __B_VT()
      __is_a(__B::__class()),
      hashCode((int32_t(*)(B)) &__Object::hashCode),
      equals((bool(*)(B, Object)) &__Object::equals),
      getClass((Class(*)(B)) &__Object::getClass),
      toString((String(*)(B)) &__Object::toString),
      A((Placeholder(*)(B,int32_t)) &__A::A),
      get(&__B::get),
      B(&__B::B)
    {
    }
  };

}
}
