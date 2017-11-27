#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::test017;

int32_t main (ArrayOfString args ){
A a = __A::__init (new __A() , 5 );
cout << a -> __vptr -> self_0 (a )-> __vptr -> toString (a -> __vptr -> self_0 (a ))-> data << endl ;
return 0 ;
}

