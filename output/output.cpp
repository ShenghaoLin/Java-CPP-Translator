#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace javalang
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
__A::A_0 (A __this , char x , char y ){
__this -> x = __this -> this.x ;
__this -> y = __this -> this.y ;
this.print ();
}

__A::A_1 (A __this , char x ){
this (__this -> this.x , (char ) __this -> this.x + 4 );
}

String __A::toString_0 (A __this ){
return new __String("A(" )+ __this -> this.x + new __String("," )+ __this -> this.y + new __String(")" );
}

void __A::overloaded_0 (A __this , int32_t i ){
cout << "overloaded(int)" << endl ;
}

void __A::overloaded_1 (A __this , byte b ){
cout << "overloaded(byte)" << endl ;
}

A __A::overloaded_2 (A __this , A a ){
cout << "overloaded(A)" << endl ;
return a ;
}

void __A::print_0 (A __this ){
cout << __this -> this.x ;
cout << "," ;
cout << __this -> this.y << endl ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.javalang.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__B::__B() : __vptr(&__vtable) {}

B __B::__init(B __this) {
return __this;
}
__B::B_0 (B __this ){
super ('x' );
}

__B::B_1 (B __this , char z ){
super (__this -> this.z );
__this -> z = __this -> this.z ;
this.overloaded (__this -> z );
}

String __B::toString_0 (B __this ){
String s= (String) -> __vptr -> null ();
return new __String("B(" )+ __this -> this.z + new __String(") extends " )+ s ;
}

A __B::overloaded_0 (B __this , B b ){
cout << "overloaded(B)" << endl ;
return b ;
}

Class __B::__class() {
static Class k = new __Class(__rt::literal("inputs.javalang.B"), __A::__class());
return k;
}

__B_VT __B::__vtable;

}
}

#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace javalang
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
int32_t __A::method_0 (A __this ){
return 12345 ;
}

String __A::toString_0 (A __this ){
return new __String("A" );
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.javalang.inputs.javalang.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__B::__B() : __vptr(&__vtable) {}

B __B::__init(B __this) {
return __this;
}
String __B::toString_0 (B __this ){
return new __String("B" );
}

Class __B::__class() {
static Class k = new __Class(__rt::literal("inputs.javalang.inputs.javalang.B"), __A::__class());
return k;
}

__B_VT __B::__vtable;

}
}

