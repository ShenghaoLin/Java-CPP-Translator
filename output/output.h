#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace test033 { 

  struct __A;
  struct __A_VT;

  struct __Test033;
  struct __Test033_VT;

  typedef __rt::Ptr<__A> A;
  typedef __rt::Ptr<__Test033> Test033;

  struct __A {

    __A_VT* __vptr;

    __A();
    static A __init(A __this);

    static int32_t m(int32_t i);

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

    __A_VT()
      __is_a(__A::__class()),
      __delete(&__rt::__delete<__A>),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString((String(*)(A)) &__Object::toString)
    {
    }
  };

  struct __Test033 {

    __Test033_VT* __vptr;

    __Test033();
    static Test033 __init(Test033 __this);

    static void main(__rt::Array<String> args);

    static Class __class();

    static __Test033_VT __vtable;
  };

  struct __Test033_VT {

    Class __is_a;

    void (*__delete)(__Test033*);
    int32_t (*hashCode)(Test033);
    bool (*equals)(Test033, Object);
    Class (*getClass)(Test033);
    String (*toString)(Test033);

    __Test033_VT()
      __is_a(__Test033::__class()),
      __delete(&__rt::__delete<__Test033>),
      hashCode((int32_t(*)(Test033)) &__Object::hashCode),
      equals((bool(*)(Test033, Object)) &__Object::equals),
      getClass((Class(*)(Test033)) &__Object::getClass),
      toString((String(*)(Test033)) &__Object::toString)
    {
    }
  };

}
}
