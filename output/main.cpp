#include "java_lang.h"
#include "output.h"
#include <iostream>

using namespace java::lang;

using namespace std;
using namespace inputs::javalang;

int32_t main (void ){
B b= (B) __B::__init (new __B ());
A a1= (A) __A::__init (new __A ());
A a2= (A) b ;
cout << a1 -> __vptr -> toString (a1 )-> data << endl ;
cout << a2 -> __vptr -> toString (a2 )-> data << endl ;
cout << a1 -> __vptr -> method (a1 )<< endl ;
cout << b -> __vptr -> method (b )<< endl ;
Class ca1= (Class) a1 -> __vptr -> getClass (a1 );
cout << ca1 -> __vptr -> toString (ca1 )-> data << endl ;
Class ca2= (Class) a2 -> __vptr -> getClass (a2 );
cout << ca2 -> __vptr -> toString (ca2 )-> data << endl ;
if (a2 -> __vptr -> getClass(a2 )-> __vptr -> isInstance( a2 -> __vptr -> getClass(a2 ), (Object) new __B ())){
cout << a2 -> __vptr -> getClass (a2 )-> __vptr -> getSuperclass (a2 -> __vptr -> getClass (a2 ))-> __vptr -> toString (a2 -> __vptr -> getClass (a2 )-> __vptr -> getSuperclass (a2 -> __vptr -> getClass (a2 )))-> data << endl ;
}

}

