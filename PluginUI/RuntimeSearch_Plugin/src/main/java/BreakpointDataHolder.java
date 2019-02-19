public class BreakpointDataHolder {

    private static BreakpointDataHolder breakpointDataHolder;
    private String file;
    // Actual line number - 1
    private int lineNumber;

    private BreakpointDataHolder() {
        file = "src/Main.java";
        lineNumber = 6;
    }

    public static BreakpointDataHolder getInstance() {
        if (breakpointDataHolder == null) {
            breakpointDataHolder = new BreakpointDataHolder();
        }
        return breakpointDataHolder;
    }

    public String getFile() {
        return file;
    }

    public int getLineNumber() {
        return lineNumber;
    }

}
