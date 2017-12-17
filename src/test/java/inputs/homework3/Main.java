package inputs.homework3;

class A {
    private char x;
    protected char y;

    public A(char x, char y) {
        this.x = x;
        this.y = y;
        print();
    }

    public A(char x) {
        this(x, (char) (x + 4));
    }

    public String toString() {
        return "A(" + x + "," + y + ")";
    }

    public void overloaded(int i) {
        System.out.println("overloaded(int)");
    }

    public void overloaded(byte b) {
        System.out.println("overloaded(byte)");
    }

    public A overloaded(A a) {
        System.out.println("overloaded(A)");
        return a;
    }

    private void print() {
        System.out.print(x);
        System.out.print(",");
        System.out.println(y);
    }
}

class B extends A {

    private char z = 42;

    public B() {
        super('x');
    }

    public B(char z) {
        super(z);
        this.z = z;
        overloaded(this.z);
    }

    public String toString() {
        String s = super.toString();
        return "B(" + z + ") extends " + s;
    }

    public A overloaded(B b) {
        System.out.println("overloaded(B)");
        return b;
    }

}

public class Main {
    public static void main(String[] args) {
        B b = new B('z');
        System.out.println(b.toString());
        b.overloaded(b).overloaded(b);
    }
}
