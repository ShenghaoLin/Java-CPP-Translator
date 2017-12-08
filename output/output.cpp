#include "output.h"
#include <iostream>

using namespace java::lang;

namespace inputs
{
namespace test006
{
__A::__A() : fld(__rt::null()), __vptr(&__vtable) {}

A __A::__init (A __this ){
__Object::__init(__this);
 __this -> fld = "A";
 return __this ;
}

void __A::setFld_String_0 (A __this , String f ){
__this -> fld = f ;
}

void __A::almostSetFld_String_0 (A __this , String f ){
String fld;
fld = f ;
}

String __A::getFld_0 (A __this ){
return __this -> fld ;
}

Class __A::__class() {
static Class k = new __Class(__rt::literal("inputs.test006.A"), __Object::__class());
return k;
}

__A_VT __A::__vtable;

__Test006::__Test006() : __vptr(&__vtable) {}

Test006 __Test006::__init(Test006 __this) {
__Object::__Object(__this);
return __this;
}

int main (__rt::Array<String> args ){
A a = __A::__init (new __A() );
a -> __vptr -> almostSetFld_String_0 (a , new __String("B" ));
cout << a -> __vptr -> getFld_0 (a )<< endl ;
a -> __vptr -> setFld_String_0 (a , new __String("B" ));
cout << a -> __vptr -> getFld_0 (a )<< endl ;
return 0 ;
}

Class __Test006::__class() {
static Class k = new __Class(__rt::literal("inputs.test006.Test006"), __Object::__class());
return k;
}

__Test006_VT __Test006::__vtable;

}
}

