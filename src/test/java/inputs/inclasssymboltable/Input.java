package inputs.inclasssymboltable;

class A {
  static int x;
  char y;
  
  static void s1() {}
}

class B extends A {
    private String fld = "B";

    public B() {
        super();
    }

    public B(char _y) {
        y = _y;
    }
  
    public static void s() {}

    static public void m(int i) {}
  
    public String m(Object o) {
        return fld;
    }

    public void test(String f, B b) {
        String fld;
        fld = f;
        m(m(b));
        s1();
        x = 1;
        y = 'a';
        m(y);
    }
}

public class Input {
    public static void main(String[] args) {
        B b = new B();
        b.test("a", b);
    }
}
