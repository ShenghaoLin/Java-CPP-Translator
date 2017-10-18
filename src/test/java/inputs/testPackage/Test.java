package inputs.testPackage;


class B extends A {
    String b;

    public B() {
        b = "B";
    }
}


public class Test {
    public static void main(String[] args) {
        B b = new B();
        System.out.println(b.a);
        System.out.println(b.b);
    }
}