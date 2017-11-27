#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test017
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
A __A::__init (A __this , int32_t x ){
__this -> self = __this ;
return __this ;
}

A __A::self_0 (A __this ){
return __this -> self ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test017.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

}
}

