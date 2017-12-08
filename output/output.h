#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs
{
namespace test021
{

struct __A;
struct __A_VT;

struct __Test021;
struct __Test021_VT;

typedef __rt::Ptr<__A> A;
typedef __rt::Ptr<__Test021> Test021;

struct __A
{

    __A_VT* __vptr;
    static int32_t x;

    __A();
    static A __init(A __this);


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

    __A_VT()
        :__is_a(__A::__class()),
         __delete(&__rt::__delete<__A>),
         hashCode((int32_t(*)(A)) &__Object::hashCode),
         equals((bool(*)(A, Object)) &__Object::equals),
         getClass((Class(*)(A)) &__Object::getClass),
         toString((String(*)(A)) &__Object::toString)
    {
    }
};

struct __Test021
{

    __Test021_VT* __vptr;

    __Test021();
    static Test021 __init(Test021 __this);

    static int_32t main(__rt::Array<String> args);

    static Class __class();

    static __Test021_VT __vtable;
};

struct __Test021_VT
{

    Class __is_a;

    void (*__delete)(__Test021*);
    int32_t (*hashCode)(Test021);
    bool (*equals)(Test021, Object);
    Class (*getClass)(Test021);
    String (*toString)(Test021);

    __Test021_VT()
        :__is_a(__Test021::__class()),
         __delete(&__rt::__delete<__Test021>),
         hashCode((int32_t(*)(Test021)) &__Object::hashCode),
         equals((bool(*)(Test021, Object)) &__Object::equals),
         getClass((Class(*)(Test021)) &__Object::getClass),
         toString((String(*)(Test021)) &__Object::toString)
    {
    }
};

}
}
