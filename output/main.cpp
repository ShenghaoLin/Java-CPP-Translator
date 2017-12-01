#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::test040;

int32_t main (ArrayOfString args ){
C c = __C::__init (new __C() );
c -> __vptr -> m (c , __A::__init (new __A() ), (Object ) c );
c -> __vptr -> m (c , c , __Object::__init (new __Object() ));
c -> __vptr -> m (c , (Object ) c , c );
return 0 ;
}

