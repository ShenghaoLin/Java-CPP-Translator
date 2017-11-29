#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test009
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
A __A::__init (A __this ){
__this -> self = __this ;
return __this ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test009.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

}
}

