#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test014
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
void __A::printOther_0 (A __this , A other ){
cout << other -> __vptr -> null (other )<< endl ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test014.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

}
}

