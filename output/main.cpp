#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::test026;

int32_t main (void ){
ArrayOfA as = new __ArrayOfB (10 );
for (int32_t i = 0 ; i < as -> length ; i ++ )
{
as -> data[i ]= __A::__init (new __A() , i );
}

int32_t k = 0 ;
while (k < 10 ){
cout << (A ) as -> data[k ]-> __vptr -> get ((A ) as -> data[k ])<< endl ;
k = k + 1 ;
}

return 0 ;
}

