#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::test014;

int32_t main (void ){
A a= (A) __A::__init (new __A ());
A other= (A) a -> some ;
a -> __vptr -> printOther_0 (a , other );
}

