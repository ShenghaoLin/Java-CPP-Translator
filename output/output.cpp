#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test024
{
__A::__A() : i(0), __vptr(&__vtable) {}

A __A::__init (A __this , int32_t i ){
__Object::__init(__this);
  __this -> i = i ;
return __this ;
}

int32_t __A::get_ (A __this ){
return __this -> i ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test024.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__Test024::__Test024() : __vptr(&__vtable) {}

Test024 __Test024::__init(Test024 __this) {
__Object::__init(__this);
return __this;
}

int32_t __Test024::main (__rt::Array<String> args ){
;
for (int32_t i = 0 ; i < ({__rt::checkNotNull(as); as->length;}) ; i ++ )
{
as -> data[i ]= __A::__init (new __A() , i );
}

;
while (k < 10 ){
cout << (A ) as -> data[k ]-> __vptr -> get ((A ) as -> data[k ])<< endl ;
k = k + 1 ;
}

return 0 ;
}

Class __Test024::__class() {
static Class k = new __Class(__rt::literal("inputs.test024.Test024"), __Object::__class());
return k;
}

__Test024_VT __Test024::__vtable;

}
}

