package edu.carleton.cs.ASEcomps;

import java.util.Arrays;

/**
 * Created by Noah on 11/1/18.
 */
public class StringSearchHolder {

    private static StringSearchHolder stringSearchHolder;
    private String stringSearch;
    private RmiServerIntf.SEARCH_TYPE searchType;
    private static final double FUZZY_ACCURACY = 0.75;
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
        if (getInstance().getStringSearch() == null || comparedString == null) {
            return false;
        }

        boolean match = false;

        switch (getInstance().searchType) {
            case STRING:
                if (comparedString.contains(getInstance().getStringSearch())) {
                    match = true;
                }
                break;
            case FUZZY:
                match = fuzzyTypeSearch(comparedString, getInstance().getStringSearch());
                break;
            case OBJECT:
                break;
            case VARIABLE:
                break;
            case REGEX:
                match = regexTypeSearch(comparedString, getInstance().getStringSearch());
                break;
        }

        if (match) {
            getInstance().matchFound = true;
            System.out.println("Match Found: Line " + lineNumber + " in " + file);
            RmiServer.getInstance().setBreakpoint(new String[]{file, String.valueOf(lineNumber - 1)});
            while (getInstance().pause);
            StringSearchHolder.getInstance().pause = true;
            getInstance().matchFound = false;
        }
        return match;
    }

    private static boolean regexTypeSearch(String comparedString, String pluginString) {
        return comparedString.matches(pluginString);
    }

    private static boolean fuzzyTypeSearch(String comparedString, String pluginString) {
        int stringLengthDif = comparedString.length() - pluginString.length();
        int containsLeven = calculateLevenshteinDistance(comparedString, pluginString) - Math.abs(stringLengthDif);
        if (containsLeven <= (Math.log(((double)pluginString.length()) * FUZZY_ACCURACY))) {
            return true;
        }
        return false;
    }

    static int calculateLevenshteinDistance(String x, String y) {
        if (x.isEmpty()) {
            return y.length();
        }

        if (y.isEmpty()) {
            return x.length();
        }

        int substitution = calculateLevenshteinDistance(x.substring(1), y.substring(1))
                + costOfSubstitution(x.charAt(0), y.charAt(0));
        int insertion = calculateLevenshteinDistance(x, y.substring(1)) + 1;
        int deletion = calculateLevenshteinDistance(x.substring(1), y) + 1;

        return min(substitution, insertion, deletion);
    }

    public static int costOfSubstitution(char a, char b) {
        return a == b ? 0 : 1;
    }

    public static int min(int... numbers) {
        return Arrays.stream(numbers).min().orElse(Integer.MAX_VALUE);
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
