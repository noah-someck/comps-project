package edu.carleton.cs.ASEcomps;

/**
 * Created by Noah on 11/1/18.
 */
public class StringSearchHolder {

    private static StringSearchHolder stringSearchHolder;
    private String stringSearch;
    private RmiServerIntf.SEARCH_TYPE searchType;
    volatile private boolean pause;
    volatile private int numCalls;
    volatile private boolean matchFound;

    private StringSearchHolder() {
        pause = true;
        matchFound = false;
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

    public void setSearchType(RmiServerIntf.SEARCH_TYPE searchType) {
        this.searchType = searchType;
    }

    public String getStringSearch() {
        return stringSearch;
    }

    public static boolean checkStringSearch(String comparedString, String className, String file, int lineNumber) {
        getInstance().incrementCalls();
        if (getInstance().getStringSearch() == null) {
            return false;
        }

        boolean match = false;

        switch (getInstance().searchType) {
            case STRING:
                if (comparedString.contains(getInstance().getStringSearch())) {
                    match = true;
                }
                break;
//            case FUZZY:
//                match = fuzzyTypeSearch(comparedString, getInstance().getStringSearch());
//                break;
            case OBJECT:
                break;
            case VARIABLE:
                break;
            case REGEX:
                match = regexTypeSearch(comparedString, getInstance().getStringSearch());
                break;
        }

        // lowercase???
        if (match) {
            getInstance().matchFound = true;
            System.out.println("Match!");
            System.out.println(className);
            System.out.println(file);
            System.out.println(lineNumber);
            RmiServer.getInstance().setBreakpoint(new String[]{file, String.valueOf(lineNumber - 1)});
            while (getInstance().pause);
            System.out.println("HELLO");
            StringSearchHolder.getInstance().pause = true;
            getInstance().matchFound = false;
        }
        // why do we do this below???
        return getInstance().getStringSearch().contains(comparedString);
    }

    private static boolean regexTypeSearch(String comparedString, String pluginString) {
        return comparedString.matches(pluginString);
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

    public boolean isMatchFound() {
        return matchFound;
    }
}
