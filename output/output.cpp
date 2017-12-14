#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test005
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
__Object::__init(__this);
return __this;
}

String __A::toString (A __this ){
return __rt::literal("A" );
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test005.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__B::__B() : __vptr(&__vtable) {}

B __B::__init(B __this) {
__Object::__init(__this);
return __this;
}

String __B::toString (B __this ){
return __rt::literal("B" );
}

Class __B::__class() {
static Class k = new __Class(__rt::literal("inputs.test005.B"), __A::__class());
return k;
}

__B_VT __B::__vtable;

__Test005::__Test005() : __vptr(&__vtable) {}

Test005 __Test005::__init(Test005 __this) {
__Object::__init(__this);
return __this;
}

int32_t __Test005::main (__rt::Array<String> args ){
B b = __B::__init (new __B() );
A a1 = __A::__init (new __A() );
A a2 = b ;
std::cout << ({A tmp  =  a1 ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> toString (tmp );
})<< std::endl ;
std::cout << ({A tmp  =  a2 ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> toString (tmp );
})<< std::endl ;
return 0 ;
}

Class __Test005::__class() {
static Class k = new __Class(__rt::literal("inputs.test005.Test005"), __Object::__class());
return k;
}

__Test005_VT __Test005::__vtable;

}
}

