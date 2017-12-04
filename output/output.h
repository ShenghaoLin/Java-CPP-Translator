#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs { 
namespace test006 { 

  struct __A;
  struct __A_VT;

  struct __Test006;
  struct __Test006_VT;

  typedef __rt::Ptr<__A> A;
  typedef __rt::Ptr<__Test006> Test006;

  struct __A {

    __A_VT* __vptr;
    String fld;
    String fld;

    __A();
    static A __init(A __this);

    static void setFld_String_0(A __this, __rt::Array<String> f);
    static void almostSetFld_String_0(A __this, __rt::Array<String> f);
    static String getFld_0(A __this);

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
    void (*setFld_String_0)(A,__rt::Array<String>);
    void (*almostSetFld_String_0)(A,__rt::Array<String>);
    String (*getFld_0)(A);

    __A_VT()
      __is_a(__A::__class()),
      __delete(&__rt::__delete<__A>),
      hashCode((int32_t(*)(A)) &__Object::hashCode),
      equals((bool(*)(A, Object)) &__Object::equals),
      getClass((Class(*)(A)) &__Object::getClass),
      toString((String(*)(A)) &__Object::toString),
      setFld_String_0(&__A::setFld_String_0),
      almostSetFld_String_0(&__A::almostSetFld_String_0),
      getFld_0(&__A::getFld_0)
    {
    }
  };

  struct __Test006 {

    __Test006_VT* __vptr;

    __Test006();
    static Test006 __init(Test006 __this);

    static void main(__rt::Array<String> args);

    static Class __class();

    static __Test006_VT __vtable;
  };

  struct __Test006_VT {

    Class __is_a;

    void (*__delete)(__Test006*);
    int32_t (*hashCode)(Test006);
    bool (*equals)(Test006, Object);
    Class (*getClass)(Test006);
    String (*toString)(Test006);

    __Test006_VT()
      __is_a(__Test006::__class()),
      __delete(&__rt::__delete<__Test006>),
      hashCode((int32_t(*)(Test006)) &__Object::hashCode),
      equals((bool(*)(Test006, Object)) &__Object::equals),
      getClass((Class(*)(Test006)) &__Object::getClass),
      toString((String(*)(Test006)) &__Object::toString)
    {
    }
  };

}
}
