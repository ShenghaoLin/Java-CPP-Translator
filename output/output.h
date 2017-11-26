#pragma once

#include "java_lang.h"

using namespace java::lang;

#include <stdint.h>
#include <string>

namespace inputs
{
namespace javalang3
{

struct __A;
struct __A_VT;

struct __B;
struct __B_VT;

typedef __rt::Ptr<__A> A;
typedef __rt::Ptr<__B> B;

struct __A
{

    __A_VT* __vptr;
    char x;
    char y;

    __A();
    static A __init(A __this);

    static String toString(A __this);
    static Placeholder A(A __this, char x, char y);
    static Placeholder A(A __this, char x);
    static Placeholder A(A __this, char x, char y);
    static Placeholder A(A __this, char x);
    static void overloaded(A __this, int32_t i);
    static void overloaded(A __this, int8_t b);
    static A overloaded(A __this, A a);
    static void overloaded(A __this, int32_t i);
    static void overloaded(A __this, int8_t b);
    static A overloaded(A __this, A a);
    static void overloaded(A __this, int32_t i);
    static void overloaded(A __this, int8_t b);
    static A overloaded(A __this, A a);
    static void print(A __this);

    static Class __class();

    static __A_VT __vtable;
};

struct __A_VT
{

    Class __is_a;

    int32_t (*hashCode)(A);
    bool (*equals)(A, Object);
    Class (*getClass)(A);
    String (*toString)(A);
    Placeholder (*A)(A,char,char);
    Placeholder (*A)(A,char);
    void (*overloaded)(A,int32_t);
    void (*overloaded)(A,int8_t);
    A (*overloaded)(A,A);

    __A_VT()
    __is_a(__A::__class()),
           hashCode((int32_t(*)(A)) &__Object::hashCode),
           equals((bool(*)(A, Object)) &__Object::equals),
           getClass((Class(*)(A)) &__Object::getClass),
           toString(&__A::toString),
           A(&__A::A),
           A(&__A::A),
           overloaded(&__A::overloaded),
           overloaded(&__A::overloaded),
           overloaded(&__A::overloaded)
    {
    }
};

struct __B
{

    __B_VT* __vptr;
    char x;
    char y;
    char z;
    String s;

    __B();
    static B __init(B __this);

    static String toString(B __this);
    static A overloaded(B __this, B b);
    static A overloaded(B __this, B b);
    static A overloaded(B __this, B b);
    static Placeholder B(B __this);
    static Placeholder B(B __this, char z);
    static Placeholder B(B __this);
    static Placeholder B(B __this, char z);

    static Class __class();

    static __B_VT __vtable;
};

struct __B_VT
{

    Class __is_a;

    int32_t (*hashCode)(B);
    bool (*equals)(B, Object);
    Class (*getClass)(B);
    String (*toString)(B);
    Placeholder (*A)(B,char,char);
    Placeholder (*A)(B,char);
    A (*overloaded)(B,B);
    A (*overloaded)(B,B);
    A (*overloaded)(B,B);
    Placeholder (*B)(B);
    Placeholder (*B)(B,char);

    __B_VT()
    __is_a(__B::__class()),
           hashCode((int32_t(*)(B)) &__Object::hashCode),
           equals((bool(*)(B, Object)) &__Object::equals),
           getClass((Class(*)(B)) &__Object::getClass),
           toString(&__B::toString),
           A((Placeholder(*)(B,char,char)) &__A::A),
           A((Placeholder(*)(B,char)) &__A::A),
           overloaded(&__B::overloaded),
           overloaded(&__B::overloaded),
           overloaded(&__B::overloaded),
           B(&__B::B),
           B(&__B::B)
    {
    }
};

}
}
