#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test026
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
A __A::__init (A __this , int32_t i ){
__this -> i = i ;
return __this ;
}

A __A::get_0 (A __this ){
return __this -> i ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test026.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__B::__B() : __vptr(&__vtable) {}

B __B::__init(B __this) {
return __this;
}
B __B::__init (B __this , int32_t i ){
__A::__init (__this , __this -> i );
return __this ;
}

B __B::get_0 (B __this ){
return 10 - __this -> i ;
}

Class __B::__class() {
static Class k = new __Class(__rt::literal("inputs.test026.B"), __A::__class());
return k;
}

__B_VT __B::__vtable;

}
}

