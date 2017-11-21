#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace test014 { 

  struct __A;
  struct __A_VT;

  typedef __rt::Ptr<__A> A;

  struct __A {

    __A_VT* __vptr;
    A some;

    __A();
    static A __init(A __this);

    static void printOther_0(A __this, A other);

    static Class __class();

    static __A_VT __vtable;
  };

  struct __A_VT {

    Class __is_a;

    int32_t (*hashCode)(A);
    bool (*equals)(A, Object);
    Class (*getClass)(A);
    String (*toString)(A);
    void (*printOther_0)(A,A);

    __A_VT()
      __is_a(__A::__class()),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString((String(*)(A)) &__Object::toString),
      printOther_0(&__A::printOther_0)
    {
    }
  };

}
}
