#include <stdint.h>
#include <string>
#include "output.h"


namespace inputs
{
namespace javalang
{
__A::__A() : __vptr(&__vtable) {}

int __A::method (A __this ){
return 12345 ;
}

String __A::toString (A __this ){
return "A" ;
}

Class __A::__class() {
static Class k = new __class(__rt::literal("inputs.javalang.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__B::__B() : __vptr(&__vtable) {}

String __B::toString (B __this ){
return "B" ;
}

Class __B::__class() {
static Class k = new __class(__rt::literal("inputs.javalang.B"), __A::__class());
return k;
}

__B_VT __B::__vtable;

__Input::__Input() : __vptr(&__vtable) {}

void main (String [] args ){
B b = __B::__init (new __B ());
A a1 = __A::__init (new __A ());
A a2 = b ;
cout << a1 -> __vptr -> toString (a1 )-> data << endl ;
cout << a2 -> __vptr -> toString (a2 )-> data << endl ;
cout << a1 -> __vptr -> method (a1 )<< endl ;
cout << b -> __vptr -> method (b )<< endl ;
Class ca1 = a1 -> __vptr -> getClass (a1 );
cout << ca1 -> __vptr -> toString (ca1 )-> data << endl ;
Class ca2 = a2 -> __vptr -> getClass (a2 );
cout << ca2 -> __vptr -> toString (ca2 )-> data << endl ;
if (a2 B ){
cout << a2 -> __vptr -> -> __vptr -> -> __vptr -> -> __vptr -> getClass (a2 , a2 , a2 , a2 )-> __vptr -> -> __vptr -> getSuperclass (a2 -> __vptr -> -> __vptr -> -> __vptr -> -> __vptr -> getClass (a2 , a2 , a2 , a2 ), a2 , a2 )-> __vptr -> toString (a2 -> __vptr -> -> __vptr -> -> __vptr -> -> __vptr -> getClass (a2 , a2 , a2 , a2 )-> __vptr -> -> __vptr -> getSuperclass (a2 -> __vptr -> -> __vptr -> -> __vptr -> -> __vptr -> getClass (a2 , a2 , a2 , a2 ), a2 , a2 ))-> data << endl ;
}

}

Class __Input::__class() {
static Class k = new __class(__rt::literal("inputs.javalang.Input"), __Object::__class());
return k;
}

__Input_VT __Input::__vtable;

}
}

