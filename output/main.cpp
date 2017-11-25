#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::javalang;

int32_t main (void )
{
    B b= __B::__init (new __B (), 'z' );
    cout << b -> __vptr -> toString_0 (b )<< endl ;
    b -> __vptr -> overloaded_0 (b )-> __vptr -> overloaded (b -> __vptr -> overloaded_0 (b ), b );
    return 0 ;
}

