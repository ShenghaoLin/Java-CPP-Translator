#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test021
{
__A::__A() : x(0), __vptr(&__vtable) {}

A __A::__init(A __this)
{
    __Object::__init(__this);
    return __this;
}

Class __A::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test021.A"), __Object::__class());
    return k;
}

A::x = 4;

__A_VT __A::__vtable;

__Test021::__Test021() : __vptr(&__vtable) {}

Test021 __Test021::__init(Test021 __this)
{
    __Object::__init(__this);
    return __this;
}

int32_t Test021::main (__rt::Array<String> args )
{
    int x;
    x = 3 ;
    cout << A ::x << endl ;
    return 0 ;
}

Class __Test021::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test021.Test021"), __Object::__class());
    return k;
}

__Test021_VT __Test021::__vtable;

}
}

