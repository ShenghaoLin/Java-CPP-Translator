#pragma once

#include <stdint.h>
#include <string>


namespace inputs{
namespace javalang {

  struct __A;
  struct __A_VT;


  typedef __A* A;


  struct __A {

    __A_VT* __vptr;
    static int32_t x;

    static __A();

    static int32_t x(A );

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
    : __is_a(__A::__class()),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString((String(*)(A)) &__Object::toString),
    {
    }
  };

}
}
