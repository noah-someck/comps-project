package edu.carleton.cs.ASEcomps;

public class Main {
    public static String var = "purple";

    private static double manySines(double d, int count) {
        for (int i=0; i<count; i++) {
            d = Math.sin(d);
        }
        return d;
    }

    private static double manyCosines(double d, int count) {
        for (int i=0; i<count; i++) {
            d = Math.cos(d);
        }
        return d;
    }

    private static void immutablecheck(String s) {
        s = "different";
    }

    public static void main(String[] args) {
        String x = "Hello";
//        Main cl = new Main();
//        String y = x;
//        x = "2";
        x += "world";
        immutablecheck(x);
        System.out.println(x);
        System.out.println(var);
//        double x = 0;
//        for (int i = 0; i<5; i++) {
//            x = manySines(x, 1_000_000);
//            try {
//                Thread.sleep(100);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            x = manyCosines(x, 3_000_000);
//        }
//
//        System.out.println("Result: " + x);
    }
}
