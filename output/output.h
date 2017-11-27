#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace test033 { 

  struct __A;
  struct __A_VT;

  typedef __rt::Ptr<__A> A;

  struct __A {

    __A_VT* __vptr;

    __A();
    static A __init(A __this);

    static int32_t m_0(int32_t i);
    static void m_1(A a);
    static void m_2(double d);
    static void m_3(Object o);
    static void m_4(Object o1, Object o2);
    static void m_5(A a1, Object o2);

    static Class __class();

    static __A_VT __vtable;
  };

  struct __A_VT {

    Class __is_a;

    int32_t (*hashCode)(A);
    bool (*equals)(A, Object);
    Class (*getClass)(A);
    String (*toString)(A);

    __A_VT()
      __is_a(__A::__class()),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString((String(*)(A)) &__Object::toString)
    {
    }
  };

}
}
