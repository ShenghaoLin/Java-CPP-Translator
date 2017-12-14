#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs
{
namespace test025
{

struct __A;
struct __A_VT;

struct __B;
struct __B_VT;

struct __Test025;
struct __Test025_VT;

typedef __rt::Ptr<__A> A;
typedef __rt::Ptr<__B> B;
typedef __rt::Ptr<__Test025> Test025;

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

struct __B
{

    __B_VT* __vptr;
    int32_t i;

    __B();
    static B __init(B __this, int32_t i);

    static int32_t get_(B __this);

    static Class __class();

    static __B_VT __vtable;
};

struct __B_VT
{

    Class __is_a;

    void (*__delete)(__B*);
    int32_t (*hashCode)(B);
    bool (*equals)(B, Object);
    Class (*getClass)(B);
    String (*toString)(B);
    int32_t (*get_)(B);

    __B_VT()
        :__is_a(__B::__class()),
         __delete(&__rt::__delete<__B>),
         hashCode((int32_t(*)(B)) &__Object::hashCode),
         equals((bool(*)(B, Object)) &__Object::equals),
         getClass((Class(*)(B)) &__Object::getClass),
         toString((String(*)(B)) &__Object::toString),
         get_(&__B::get_)
    {
    }
};

struct __Test025
{

    __Test025_VT* __vptr;

    __Test025();
    static Test025 __init(Test025 __this);

    static int32_t main(__rt::Array<String> args);

    static Class __class();

    static __Test025_VT __vtable;
};

struct __Test025_VT
{

    Class __is_a;

    void (*__delete)(__Test025*);
    int32_t (*hashCode)(Test025);
    bool (*equals)(Test025, Object);
    Class (*getClass)(Test025);
    String (*toString)(Test025);

    __Test025_VT()
        :__is_a(__Test025::__class()),
         __delete(&__rt::__delete<__Test025>),
         hashCode((int32_t(*)(Test025)) &__Object::hashCode),
         equals((bool(*)(Test025, Object)) &__Object::equals),
         getClass((Class(*)(Test025)) &__Object::getClass),
         toString((String(*)(Test025)) &__Object::toString)
    {
    }
};

}
}
