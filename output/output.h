#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs
{
namespace test007
{

struct __A;
struct __A_VT;

struct __B;
struct __B_VT;

struct __Test007;
struct __Test007_VT;

typedef __rt::Ptr<__A> A;
typedef __rt::Ptr<__B> B;
typedef __rt::Ptr<__Test007> Test007;

struct __A
{

    __A_VT* __vptr;
    String a;

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

struct __B
{

    __B_VT* __vptr;
    String a;
    String b;

    __B();
    static B __init(B __this);


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

    __B_VT()
        :__is_a(__B::__class()),
         __delete(&__rt::__delete<__B>),
         hashCode((int32_t(*)(B)) &__Object::hashCode),
         equals((bool(*)(B, Object)) &__Object::equals),
         getClass((Class(*)(B)) &__Object::getClass),
         toString((String(*)(B)) &__Object::toString)
    {
    }
};

struct __Test007
{

    __Test007_VT* __vptr;

    __Test007();
    static Test007 __init(Test007 __this);

    static int32_t main(__rt::Array<String> args);

    static Class __class();

    static __Test007_VT __vtable;
};

struct __Test007_VT
{

    Class __is_a;

    void (*__delete)(__Test007*);
    int32_t (*hashCode)(Test007);
    bool (*equals)(Test007, Object);
    Class (*getClass)(Test007);
    String (*toString)(Test007);

    __Test007_VT()
        :__is_a(__Test007::__class()),
         __delete(&__rt::__delete<__Test007>),
         hashCode((int32_t(*)(Test007)) &__Object::hashCode),
         equals((bool(*)(Test007, Object)) &__Object::equals),
         getClass((Class(*)(Test007)) &__Object::getClass),
         toString((String(*)(Test007)) &__Object::toString)
    {
    }
};

}
}
