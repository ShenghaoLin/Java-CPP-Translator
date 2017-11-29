#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace test009 { 

  struct __A;
  struct __A_VT;

  struct __Test009;
  struct __Test009_VT;

  typedef __rt::Ptr<__A> A;
  typedef __rt::Ptr<__Test009> Test009;

  struct __A {

    __A_VT* __vptr;
    A self;

    __A();
    static A __init(A __this);


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

  struct __Test009 {

    __Test009_VT* __vptr;

    __Test009();
    static Test009 __init(Test009 __this);

    static void main(__rt::Array<String> args);

    static Class __class();

    static __Test009_VT __vtable;
  };

  struct __Test009_VT {

    Class __is_a;

    void (*__delete)(__Test009*);
    int32_t (*hashCode)(Test009);
    bool (*equals)(Test009, Object);
    Class (*getClass)(Test009);
    String (*toString)(Test009);

    __Test009_VT()
      __is_a(__Test009::__class()),
      __delete(&__rt::__delete<__Test009>),
      hashCode((int32_t(*)(Test009)) &__Object::hashCode),
      equals((bool(*)(Test009, Object)) &__Object::equals),
      getClass((Class(*)(Test009)) &__Object::getClass),
      toString((String(*)(Test009)) &__Object::toString)
    {
    }
  };

}
}
