#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test011
{
__A::__A() : a(__rt::null()), __vptr(&__vtable) {}

A __A::__init(A __this) {
__Object::__init(__this);
return __this;
}

void __A::setA_String_ (A __this , String x ){
__this -> a = x ;
}

void __A::printOther_A_ (A __this , A other ){
std::cout << other -> a << std::endl ;
}

String __A::toString (A __this ){
return __this -> a ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test011.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__B1::__B1() : a(__rt::null()), b(__rt::null()), __vptr(&__vtable) {}

B1 __B1::__init(B1 __this) {
__A::__init(__this);
return __this;
}

Class __B1::__class() {
static Class k = new __Class(__rt::literal("inputs.test011.B1"), __A::__class());
return k;
}

__B1_VT __B1::__vtable;

__B2::__B2() : a(__rt::null()), b(__rt::null()), __vptr(&__vtable) {}

B2 __B2::__init(B2 __this) {
__A::__init(__this);
return __this;
}

Class __B2::__class() {
static Class k = new __Class(__rt::literal("inputs.test011.B2"), __A::__class());
return k;
}

__B2_VT __B2::__vtable;

__C::__C() : a(__rt::null()), b(__rt::null()), c(__rt::null()), __vptr(&__vtable) {}

C __C::__init(C __this) {
__B1::__init(__this);
return __this;
}

Class __C::__class() {
static Class k = new __Class(__rt::literal("inputs.test011.C"), __B1::__class());
return k;
}

__C_VT __C::__vtable;

__Test011::__Test011() : __vptr(&__vtable) {}

Test011 __Test011::__init(Test011 __this) {
__Object::__init(__this);
return __this;
}

int32_t __Test011::main (__rt::Array<String> args ){
A a = __A::__init (new __A() );
({A tmp  =  a ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> setA_String_ (tmp , __rt::literal("A" ));
});
B1 b1 = __B1::__init (new __B1() );
({B1 tmp  =  b1 ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> setA_String_ (tmp , __rt::literal("B1" ));
});
B2 b2 = __B2::__init (new __B2() );
({B2 tmp  =  b2 ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> setA_String_ (tmp , __rt::literal("B2" ));
});
C c = __C::__init (new __C() );
({C tmp  =  c ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> setA_String_ (tmp , __rt::literal("C" ));
});
({A tmp  =  a ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> printOther_A_ (tmp , a );
});
({A tmp  =  a ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> printOther_A_ (tmp , b1 );
});
({A tmp  =  a ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> printOther_A_ (tmp , b2 );
});
({A tmp  =  a ;
__rt::checkNotNull(tmp );
tmp -> __vptr -> printOther_A_ (tmp , c );
});
return 0 ;
}

Class __Test011::__class() {
static Class k = new __Class(__rt::literal("inputs.test011.Test011"), __Object::__class());
return k;
}

__Test011_VT __Test011::__vtable;

}
}

