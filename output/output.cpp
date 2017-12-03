#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace javalang3
{
__A::__A() : x(0), y(0), __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
A __A::__init (A __this , char x , char y ){
__Object::__init(__this);
 __this -> x = x ;
__this -> y = y ;
__this -> __vptr -> print (__this );
return __this ;
}

A __A::__init (A __this , char x ){
__A::__init (__this , x , (char ) x + 4 );
return __this ;
}

A __A::toString (A __this ){
return new __String("A(" )+ __this -> x + new __String("," )+ __this -> y + new __String(")" );
}

A __A::overloaded _int_0 (A __this , int32_t i ){
cout << "overloaded(int)" << endl ;
}

A __A::overloaded _byte_0 (A __this , byte b ){
cout << "overloaded(byte)" << endl ;
}

A __A::overloaded _A_0 (A __this , A a ){
cout << "overloaded(A)" << endl ;
return a ;
}

A __A::print _0 (A __this ){
cout << __this -> x ;
cout << "," ;
cout << __this -> y << endl ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.javalang3.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__B::__B() : x(0), y(0), z(42), __vptr(&__vtable) {}

B __B::__init(B __this) {
return __this;
}
B __B::__init (B __this ){
__A::__init (__this , 'x' );
return __this ;
}

B __B::__init (B __this , char z ){
__A::__init (__this , z );
z = z ;
__this -> __vptr -> overloaded (__this , __this -> z );
return __this ;
}

B __B::toString (B __this ){
String s = (new __A()) -> __vptr -> toString ((A) __this );
return new __String("B(" )+ __this -> z + new __String(") extends " )+ s ;
}

B __B::overloaded _B_0 (B __this , B b ){
cout << "overloaded(B)" << endl ;
return b ;
}

Class __B::__class() {
static Class k = new __Class(__rt::literal("inputs.javalang3.B"), __A::__class());
return k;
}

__B_VT __B::__vtable;

}
}

