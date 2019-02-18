package edu.carleton.cs.ASEcomps;

/**
 * Created by Noah on 11/1/18.
 */
public class StringSearchHolder {

    private static StringSearchHolder stringSearchHolder;
    private String stringSearch;
    volatile private boolean pause;
    volatile private int numCalls;

    private StringSearchHolder() {
        pause = true;
        numCalls = 0;
    }

    public static StringSearchHolder getInstance() {
        if (stringSearchHolder == null) {
            stringSearchHolder = new StringSearchHolder();
        }
        return stringSearchHolder;
    }

    public void setStringSearch(String stringSearch) {
        this.stringSearch = stringSearch;
    }

    public String getStringSearch() {
        return stringSearch;
    }

    public static boolean checkStringSearch(String comparedString, String className, String file, int lineNumber) {
        getInstance().incrementCalls();
        System.out.println(className);
        if (getInstance().getStringSearch() == null) {
            return false;
        }
        // lowercase???
        if (comparedString.contains(getInstance().getStringSearch())) {
            System.out.println("Match!");
            System.out.println(className);
            System.out.println(file);
            System.out.println(lineNumber);
            RmiServer.getInstance().setBreakpoint(new String[]{file, String.valueOf(lineNumber - 1)});
            while (getInstance().pause);
            System.out.println("HELLO");
            StringSearchHolder.getInstance().pause = true;
        }
        // why do we do this below???
        return getInstance().getStringSearch().contains(comparedString);
    }

    public void continueSearch() {
        pause = false;
    }

    public void incrementCalls() {
        numCalls++;
    }

    public int getNumCalls() {
        return numCalls;
    }

    public boolean paused() {
        return pause;
    }
}
