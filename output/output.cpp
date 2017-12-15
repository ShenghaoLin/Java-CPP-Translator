#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test028
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
    static Class k = new __Class(__rt::literal("inputs.test028.A"), __Object::__class());
    return k;
}

__A_VT __A::__vtable;

__Test028::__Test028() : __vptr(&__vtable) {}

Test028 __Test028::__init(Test028 __this)
{
    __Object::__init(__this);
    return __this;
}

int32_t __Test028::main (__rt::Array<String> args )
{
    __rt::Array<A> as = __rt::__Array<A>::__init(new __rt::__Array<A>(__rt::checkNegativeIndex(10))) ;
    std::cout << ({Class tmp  =  ({__rt::Array<A> tmp  =  as ;
                                   __rt::checkNotNull(tmp );
                                   tmp -> __vptr -> getClass (tmp );
                                  });
                   __rt::checkNotNull(tmp );
                   tmp -> __vptr -> toString (tmp );
                  })<< std::endl ;
    return 0 ;
}

Class __Test028::__class()
{
    static Class k = new __Class(__rt::literal("inputs.test028.Test028"), __Object::__class());
    return k;
}

__Test028_VT __Test028::__vtable;

}
}

