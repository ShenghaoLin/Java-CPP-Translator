#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::test009;

int32_t main (ArrayOfString args ){
A a = __A::__init (new __A() );
cout << a -> self -> __vptr -> toString (a -> self )-> data << endl ;
return 0 ;
}

