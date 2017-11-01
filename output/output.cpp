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
int32_t __A::method (A __this ){
return 12345 ;
}

String __A::toString (A __this ){
return new __String("A" );
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
String __B::toString (B __this ){
return new __String("B" );
}

Class __B::__class() {
static Class k = new __Class(__rt::literal("inputs.javalang.B"), __A::__class());
return k;
}

__B_VT __B::__vtable;

}
}

