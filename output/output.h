#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs
{
namespace test028
{

struct __A;
struct __A_VT;

struct __Test028;
struct __Test028_VT;

typedef __rt::Ptr<__A> A;
typedef __rt::Ptr<__Test028> Test028;

struct __A
{

    __A_VT* __vptr;
    int32_t i;

    __A();
    static A __init(A __this, int32_t i);

    static int32_t get_(A __this);

    static Class __class();

    static __A_VT __vtable;
};

struct __A_VT
{

    Class __is_a;

    void (*__delete)(__A*);
    int32_t (*hashCode)(A);
    bool (*equals)(A, Object);
    Class (*getClass)(A);
    String (*toString)(A);
    int32_t (*get_)(A);

    __A_VT()
        :__is_a(__A::__class()),
         __delete(&__rt::__delete<__A>),
         hashCode((int32_t(*)(A)) &__Object::hashCode),
         equals((bool(*)(A, Object)) &__Object::equals),
         getClass((Class(*)(A)) &__Object::getClass),
         toString((String(*)(A)) &__Object::toString),
         get_(&__A::get_)
    {
    }
};

struct __Test028
{

    __Test028_VT* __vptr;

    __Test028();
    static Test028 __init(Test028 __this);

    static int32_t main(__rt::Array<String> args);

    static Class __class();

    static __Test028_VT __vtable;
};

struct __Test028_VT
{

    Class __is_a;

    void (*__delete)(__Test028*);
    int32_t (*hashCode)(Test028);
    bool (*equals)(Test028, Object);
    Class (*getClass)(Test028);
    String (*toString)(Test028);

    __Test028_VT()
        :__is_a(__Test028::__class()),
         __delete(&__rt::__delete<__Test028>),
         hashCode((int32_t(*)(Test028)) &__Object::hashCode),
         equals((bool(*)(Test028, Object)) &__Object::equals),
         getClass((Class(*)(Test028)) &__Object::getClass),
         toString((String(*)(Test028)) &__Object::toString)
    {
    }
};

}
}
