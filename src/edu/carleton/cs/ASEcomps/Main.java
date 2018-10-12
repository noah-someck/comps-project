package edu.carleton.cs.ASEcomps;

import jdk.internal.org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;

public class Main {

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

    public static void main(String[] args) {
        double x = 0;
        for (int i = 0; i<5; i++) {
            x = manySines(x, 1_000_000);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            x = manyCosines(x, 3_000_000);
        }

        System.out.println("Result: " + x);
        //System.out.println("Spent " + (System.currentTimeMillis() - time) + " milliseconds.");
    }
}
