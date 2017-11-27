#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::test033;

int32_t main (void ){
A a= (A) __A::__init (new __A ());
byte b= 1 ;
A -> __vptr -> m (A , b );
A -> __vptr -> m (A , a );
A -> __vptr -> m (A , 1.0 );
A -> __vptr -> m (A , (Object ) a );
A -> __vptr -> m (A , new __A (), a );
A -> __vptr -> m (A , new __Object (), a );
}

