package edu.carleton.cs.ASEcomps;

public class Main {

    public static void main(String[] args) {
        //long time = System.currentTimeMillis();
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Done Main.");
        //System.out.println("Spent " + (System.currentTimeMillis() - time) + " milliseconds.");
    }
}
