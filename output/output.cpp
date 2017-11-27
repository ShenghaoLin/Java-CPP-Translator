#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test033
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this) {
return __this;
}
int32_t __A::m_0 (A __this , int32_t i ){
cout << "A.m(int)" << endl ;
return i ;
}

void __A::m_1 (A __this , A a ){
cout << "A.m(A)" << endl ;
}

void __A::m_2 (A __this , double d ){
cout << "A.m(double)" << endl ;
}

void __A::m_3 (A __this , Object o ){
cout << "A.m(Object)" << endl ;
}

void __A::m_4 (A __this , Object o1 , Object o2 ){
cout << "A.m(Object, Object)" << endl ;
}

void __A::m_5 (A __this , A a1 , Object o2 ){
cout << "A.m(A, Object)" << endl ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test033.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

}
}

