#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::javalang3;

int32_t main (__rt::Array<String> args )
{
    __rt::Array<int32_t> a = new __rt::Array<int32_t> (10 );
    __rt::Array<A> aaa = new __rt::Array<A> (10 );
    B b = __B::__init (new __B(), 'z' );
    cout << b -> __vptr -> toString (b )-> data << endl ;
    b -> __vptr -> overloaded (b, b )-> __vptr -> overloaded (b -> __vptr -> overloaded (b, b ), b );
    return 0 ;
}

