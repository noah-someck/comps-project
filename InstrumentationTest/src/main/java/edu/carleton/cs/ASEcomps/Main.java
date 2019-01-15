package edu.carleton.cs.ASEcomps;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class Main {
//    public static String var = "Helloworld";

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

    private static String returnString() {
        return "test";
    }

    private static String[] returnStringArray() {
        String x = "Helloworld";
        String[] test = new String[1];
        test[0] = x;
        return test;
    }

    private static int[] returnIntArray() {
        int x = 1;
        int[] test = new int[1];
        test[0] = x;
        return test;
    }

    private static void immutablecheck(String s) {
        s = "different";
    }

    public static void main(String[] args) {
//        String test = "Monkey";
//        String x = "Hello";
//
//        x += "world";
//        immutablecheck(x);
//
//        returnString();

//        String x = "Helloworld";
//        String[] test = new String[1];
//        test[0] = x;
//
//        String[] stringArray = returnStringArray();
//        String y = stringArray[0];
        Object str = "Helloworld";
        String string = (String) str;
        str = "yes";

        RuntimeMXBean bean = ManagementFactory.getRuntimeMXBean();
        List<String> jvmArgs = bean.getInputArguments();

        for (int i = 0; i < jvmArgs.size(); i++) {
            System.out.println( jvmArgs.get( i ) );
        }

        System.out.println(System.getProperty("java.class.path"));

//        int[] intArray = returnIntArray();
//        int y = intArray[0];
    }
}
