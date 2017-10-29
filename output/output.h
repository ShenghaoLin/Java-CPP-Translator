#pragma once

#include "java_lang.h"

using namespace java::lang;

namespace inputs {

struct __A;
struct __A_VT;

struct __B;
struct __B_VT;

typedef __A* A;
typedef __B* B;

struct __A {

__A_VT* __vptr ;
static __A_VT __vtable ;

 __A();

static Class __class();
int32_t method(A );
String toString(A );

};

struct __A_VT {

Class __is_a;

int32_t (*hashCode)(A);
bool (*equals)(A, Object);
Class (*getClass)(A);
String (*toString)(A,A);
int32_t (*method)(A,A);

__A_VT()
:__is_a(__A::__class()),
hashCode((int32_t(*)(A)) &__Object::hashCode),
equals((bool(*)(A, Object)) &__Object::equals),
getClass((Class(*)(A)) &__Object::getClass),
toString(&__A::toString),
method(&__A::method)
{
}
};
};
struct __B {

__B_VT* __vptr ;
static __B_VT __vtable ;

 __B();

static Class __class();
String toString(B );

};

struct __B_VT {

Class __is_a;

int32_t (*hashCode)(B);
bool (*equals)(B, Object);
Class (*getClass)(B);
String (*toString)(B,B);
int32_t (*method)(B,A);

__B_VT()
:__is_a(__B::__class()),
hashCode((int32_t(*)(B)) &__Object::hashCode),
equals((bool(*)(B, Object)) &__Object::equals),
getClass((Class(*)(B)) &__Object::getClass),
toString(&__B::toString),
method((int32_t(*)(B,A)) &__A::method)
{
}
};
};
