package edu.carleton.cs.ASEcomps;

public class StringChecker {
    public static void check(String toCheck, String methodName, String textified) {
        System.out.println("In " + methodName + ", found \"" + toCheck + "\" as result of instruction:");
        System.out.println(textified);
    }
}
