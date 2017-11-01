#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace input { 

  struct __A;
  struct __A_VT;

  struct __B;
  struct __B_VT;


  typedef __A* A;
  typedef __B* B;


  struct __A {

    __A_VT* __vptr;

    __A();
    static A __init(A __this);

    static String toString(A );
    static int32_t method(A );

    static Class __class();

    static __A_VT __vtable;
  };

  struct __A_VT {

    Class __is_a;

    int32_t (*hashCode)(A);
    bool (*equals)(A, Object);
    Class (*getClass)(A);
    String (*toString)(A);
    int32_t (*method)(A);

    __A_VT()
    : __is_a(__A::__class()),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString(&__A::toString),
      method(&__A::method)
    {
    }
  };

  struct __B {

    __B_VT* __vptr;

    __B();
    static B __init(B __this);

    static String toString(B );

    static Class __class();

    static __B_VT __vtable;
  };

  struct __B_VT {

    Class __is_a;

    int32_t (*hashCode)(B);
    bool (*equals)(B, Object);
    Class (*getClass)(B);
    String (*toString)(B);
    int32_t (*method)(B);

    __B_VT()
    : __is_a(__B::__class()),
      hashCode((int32_t(*)(B)) &__Object::hashCode),
      equals((bool(*)(B, Object)) &__Object::equals),
      getClass((Class(*)(B)) &__Object::getClass),
      toString(&__B::toString),
      method((int32_t(*)(B)) &__A::method)
    {
    }
  };

}
}
