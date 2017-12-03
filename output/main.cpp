#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::javalang3;

int32_t main (ArrayOfString args ){
B b = __B::__init (new __B() , 'z' );
cout << b -> __vptr -> toString (b )-> data << endl ;
b -> __vptr -> overloaded (b , b )-> __vptr -> overloaded (b -> __vptr -> overloaded (b , b ), b );
return 0 ;
}

