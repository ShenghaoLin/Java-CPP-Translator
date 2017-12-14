#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test007
{
__A::__A() : a(__rt::null()), __vptr(&__vtable) {}

A __A::__init (A __this )
{
    __Object::__init(__this);
    __this -> a = __rt::literal("A" );
    return __this ;
}

Class __A::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test007.A"), __Object::__class());
    return k;
}

__A_VT __A::__vtable;

__B::__B() : a(__rt::null()), b(__rt::null()), __vptr(&__vtable) {}

B __B::__init (B __this )
{
    __A::__init(__this);
    __this -> b = __rt::literal("B" );
    return __this ;
}

Class __B::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test007.B"), __A::__class());
    return k;
}

__B_VT __B::__vtable;

__Test007::__Test007() : __vptr(&__vtable) {}

Test007 __Test007::__init(Test007 __this)
{
    __Object::__init(__this);
    return __this;
}

int32_t __Test007::main (__rt::Array<String> args )
{
    B b = __B::__init (new __B() );
    std::cout << b -> a << std::endl ;
    std::cout << b -> b << std::endl ;
    return 0 ;
}

Class __Test007::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test007.Test007"), __Object::__class());
    return k;
}

__Test007_VT __Test007::__vtable;

}
}

