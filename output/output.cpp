#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test002
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
A __A::toString (A __this ){
return new __String("A" );
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test002.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__Test002::__Test002() : __vptr(&__vtable) {}

Test002 __Test002::__init(Test002 __this) {
return __this;
}
int main (__rt::Array<String> args ){
A a = __A::__init (new __A() );
Object o = a ;
cout << o -> __vptr -> toString (o )<< endl ;
return 0 ;
}

Class __Test002::__class() {
static Class k = new __Class(__rt::literal("inputs.test002.Test002"), __Object::__class());
return k;
}

__Test002_VT __Test002::__vtable;

}
}

