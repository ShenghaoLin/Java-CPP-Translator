#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace javalang
{
__A::__A() : __vptr(&__vtable) {}

A __A::__init(A __this)
{
    return __this;
}
A __A::__init (A __this, char x, char y )
{
    __this -> x = x ;
    __this -> y = y ;
    __this -> __vptr -> print_0 (__this );
    return __this ;
}

A __A::__init (A __this, char x )
{
    __A::__init (__this, x, (char ) x + 4 );
    return __this ;
}

A __A::toString_0 (A __this )
{
    return new __String("A(" )+ __this -> x + new __String("," )+ __this -> y + new __String(")" );
}

A __A::overloaded_0 (A __this, int32_t i )
{
    cout << "overloaded(int)" << endl ;
}

A __A::overloaded_1 (A __this, byte b )
{
    cout << "overloaded(byte)" << endl ;
}

A __A::overloaded_2 (A __this, A a )
{
    cout << "overloaded(A)" << endl ;
    return a ;
}

A __A::print_0 (A __this )
{
    cout << __this -> x ;
    cout << "," ;
    cout << __this -> y << endl ;
}

Class __A::__class()
{
    static Class k = new __Class(__rt::literal("inputs.javalang.A"), __Object::__class());
    return k;
}

__A_VT __A::__vtable;

__B::__B() : __vptr(&__vtable) {}

B __B::__init(B __this)
{
    return __this;
}
B __B::__init (B __this )
{
    __A::__init (__this, 'x' );
    return __this ;
}

B __B::__init (B __this, char z )
{
    __A::__init (__this, z );
    __this -> z = z ;
    __this -> __vptr -> overloaded_0 (__this, __this -> z );
    return __this ;
}

B __B::toString_0 (B __this )
{
    String s= ((A) __this) -> __vptr -> toString_0 (((A) __this) );
    return new __String("B(" )+ __this -> z + new __String(") extends " )+ s ;
}

B __B::overloaded_0 (B __this, B b )
{
    cout << "overloaded(B)" << endl ;
    return b ;
}

Class __B::__class()
{
    static Class k = new __Class(__rt::literal("inputs.javalang.B"), __A::__class());
    return k;
}

__B_VT __B::__vtable;

}
}

