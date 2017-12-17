package inputs.inclassarrays;

class A {
    public String toString() {
        return "A";
    }
}

public class Input {
    public static void main(String[] args) {
        int[] ints = new int[2];
        System.out.println(ints[1]);

        float[] floats = new float[2];
        System.out.println(floats[1]);

        A[] array = new A[5];

        for (int i = 0; i < array.length; ++i) {
            A a = new A();
            array[i] = a; // Don't forget array store check!
        }

        for (int i = 0; i < array.length; ++i) {
            A a = array[i];
            System.out.println(a.toString());
        }

        try {
            System.out.println(array[128]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Caught ArrayIndexOutOfBoundsException");
        }

        try {
            Object[] oa = array;
            oa[2] = new Object(); // Don't forget array store check!
        } catch (ArrayStoreException e) {
            System.out.println("Caught ArrayStoreException");
        }

        try {
            // Hint: translate Object[][] to Array<Array<Object>>
            Object[][] oaa = new Object[1][];
            System.out.println(oaa.getClass().getName());
            oaa[0] = new A[1]; // Don't forget array store check!
        } catch (ArrayStoreException e) {
            System.out.println("We did not implement the array covariance check correctly!");
        }
    }
}