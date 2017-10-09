package test;

public class Test {

    public static void main(String[] a) {
        int c = 0;
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < 2; x++) {
                for (int z = 0; z < 2; z++) {
                    System.out.println(c++ + ": " + y + " " + x + " " + z);
                }
            }
        }
    }
}
