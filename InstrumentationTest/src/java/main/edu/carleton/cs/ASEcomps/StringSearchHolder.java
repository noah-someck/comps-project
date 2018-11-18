package edu.carleton.cs.ASEcomps;

/**
 * Created by Noah on 11/1/18.
 */
public class StringSearchHolder {

    private static StringSearchHolder stringSearchHolder;
    private String stringSearch;

    private StringSearchHolder() {
        stringSearch = "Helloworld";
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

    public static boolean checkStringSearch(String comparedString, String className) {
        if (comparedString.contains(getInstance().getStringSearch())) {
            System.out.println("Match!");
            System.out.println(className);
        }
        return getInstance().getStringSearch().contains(comparedString);
    }
}
