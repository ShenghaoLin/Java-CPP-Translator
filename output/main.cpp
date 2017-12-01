#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::test005;

int32_t main (ArrayOfString args ){
B b = __B::__init (new __B() );
A a1 = __A::__init (new __A() );
A a2 = b ;
cout << a1 -> __vptr -> toString (a1 )-> data << endl ;
cout << a2 -> __vptr -> toString (a2 )-> data << endl ;
return 0 ;
}

