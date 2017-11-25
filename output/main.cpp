#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::javalang::inputs::javalang;

int32_t main (void ){
B b= (B) __B::__init (new __B ());
A a1= (A) __A::__init (new __A ());
A a2= (A) b ;
cout << a1 -> __vptr -> toString_0 (a1 )<< endl ;
cout << a2 -> __vptr -> toString_0 (a2 )<< endl ;
cout << a1 -> __vptr -> method_0 (a1 )<< endl ;
cout << b -> __vptr -> method_0 (b )<< endl ;
Class ca1= (Class) a1 -> __vptr -> null (a1 );
cout << ca1 -> __vptr -> null (ca1 )<< endl ;
Class ca2= (Class) a2 -> __vptr -> null (a2 );
cout << ca2 -> __vptr -> null (ca2 )<< endl ;
if (a2 -> __vptr -> getClass(a2 )-> __vptr -> isInstance( a2 -> __vptr -> getClass(a2 ), (Object) new __B ())){
cout << a2 -> __vptr -> null (a2 )-> __vptr -> null (a2 -> __vptr -> null (a2 ))-> __vptr -> null (a2 -> __vptr -> null (a2 )-> __vptr -> null (a2 -> __vptr -> null (a2 )))<< endl ;
}

}

