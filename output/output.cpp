#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test012
{
__A::__A() : a(__rt::null()), __vptr(&__vtable) {}

A __A::__init(A __this) {
__Object::__init(__this);
return __this;
}

void __A::setA_String_0 (A __this , String x ){
__this -> a = x ;
}

void __A::printOther_A_0 (A __this , A other ){
cout << other -> __vptr -> myToString_0 (other )<< endl ;
}

String __A::myToString_0 (A __this ){
return __this -> a ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test012.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__B1::__B1() : a(__rt::null()), b(__rt::null()), __vptr(&__vtable) {}

B1 __B1::__init(B1 __this) {
__Object::__init(__this);
return __this;
}

Class __B1::__class() {
static Class k = new __Class(__rt::literal("inputs.test012.B1"), __A::__class());
return k;
}

__B1_VT __B1::__vtable;

__B2::__B2() : a(__rt::null()), b(__rt::null()), __vptr(&__vtable) {}

B2 __B2::__init(B2 __this) {
__Object::__init(__this);
return __this;
}

Class __B2::__class() {
static Class k = new __Class(__rt::literal("inputs.test012.B2"), __A::__class());
return k;
}

__B2_VT __B2::__vtable;

__C::__C() : a(__rt::null()), b(__rt::null()), c(__rt::null()), __vptr(&__vtable) {}

C __C::__init(C __this) {
__Object::__init(__this);
return __this;
}

String __C::myToString1 (C __this ){
return new __String("still C" );
}

Class __C::__class() {
static Class k = new __Class(__rt::literal("inputs.test012.C"), __B1::__class());
return k;
}

__C_VT __C::__vtable;

__Test012::__Test012() : __vptr(&__vtable) {}

Test012 __Test012::__init(Test012 __this) {
__Object::__init(__this);
return __this;
}

int main (__rt::Array<String> args ){
A a = __A::__init (new __A() );
a -> __vptr -> setA_String_0 (a , new __String("A" ));
B1 b1 = __B1::__init (new __B1() );
b1 -> __vptr -> setA_String_0 (b1 , new __String("B1" ));
B2 b2 = __B2::__init (new __B2() );
b2 -> __vptr -> setA_String_0 (b2 , new __String("B2" ));
C c = __C::__init (new __C() );
c -> __vptr -> setA_String_0 (c , new __String("C" ));
a -> __vptr -> printOther_A_0 (a , a );
a -> __vptr -> printOther_A_0 (a , b1 );
a -> __vptr -> printOther_A_0 (a , b2 );
a -> __vptr -> printOther_A_0 (a , c );
return 0 ;
}

Class __Test012::__class() {
static Class k = new __Class(__rt::literal("inputs.test012.Test012"), __Object::__class());
return k;
}

__Test012_VT __Test012::__vtable;

}
}

