#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::test033;

int32_t main (ArrayOfString args ){
A a = __A::__init (new __A() );
byte b = 1 ;
A -> __vptr -> m_0 (A , b );
A -> __vptr -> m_1 (A , a );
A -> __vptr -> m_2 (A , 1.0 );
A -> __vptr -> m_3 (A , (Object ) a );
A -> __vptr -> m_5 (A , __A::__init (new __A() ), a );
A -> __vptr -> m_4 (A , __Object::__init (new __Object() ), a );
return 0 ;
}

