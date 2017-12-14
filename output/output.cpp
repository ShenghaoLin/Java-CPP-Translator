#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test025
{
__A::__A() : i(0), __vptr(&__vtable) {}

A __A::__init (A __this, int32_t i )
{
    __Object::__init(__this);
    __this -> i = i ;
    return __this ;
}

int32_t __A::get_ (A __this )
{
    return __this -> i ;
}

Class __A::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test025.A"), __Object::__class());
    return k;
}

__A_VT __A::__vtable;

__B::__B() : i(0), __vptr(&__vtable) {}

B __B::__init (B __this, int32_t i )
{
    __A::__init (__this, i );
    return __this ;
}

int32_t __B::get_ (B __this )
{
    return 10 - __this -> i ;
}

Class __B::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test025.B"), __A::__class());
    return k;
}

__B_VT __B::__vtable;

__Test025::__Test025() : __vptr(&__vtable) {}

Test025 __Test025::__init(Test025 __this)
{
    __Object::__init(__this);
    return __this;
}

int32_t __Test025::main (__rt::Array<String> args )
{
    __rt::Array<Object> as = new __rt::Array<A> (10);
    for (int32_t i = 0 ; i < ({__rt::checkNotNull(as); as->length;}) ; i ++ )
    {
        Object tmp = __B::__init (new __B(), i );
        __rt::arrayStoreCheck(as, i, tmp );
        as -> __data[i ]= tmp ;
    }

    int32_t k = 0 ;
    while (k < 10 )
    {
        std::cout << ({A tmp  =  __rt::java_cast<A> (({__rt::arrayAccessCheck(as, k);
                                 as -> __data[k ];
                                                      })) ;
                       __rt::checkNotNull(tmp );
                       tmp -> __vptr -> get_ (tmp );
                      })<< std::endl ;
        k = k + 1 ;
    }

    return 0 ;
}

Class __Test025::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test025.Test025"), __Object::__class());
    return k;
}

__Test025_VT __Test025::__vtable;

}
}

