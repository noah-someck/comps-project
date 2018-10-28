package edu.carleton.cs.ASEcomps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings("unused")
public class ExternalProfileAccumulator {
    private static Map<String, Long> methodTimes = new ConcurrentHashMap<>();
    private static Map<String, Integer> methodCalls = new ConcurrentHashMap<>();

    public static void recordMethodUse(String methodName, long timeInMethod) {
        methodTimes.merge(methodName, timeInMethod, Long::sum);
        methodCalls.merge(methodName, 1, Integer::sum);
    }

    public static String getReport() {
        StringBuilder sb = new StringBuilder("Method Usage Summary:\n");
        for (String methodName : methodTimes.keySet()) {
            int calls = methodCalls.get(methodName);
            long timeSpent = methodTimes.get(methodName);
            sb.append(methodName).append(" was called ").append(calls);
            if (calls == 1) {
                sb.append(" time.\n");
            } else {
                sb.append(" times.\n");
            }
            sb.append("    Total time spent: ").append(timeSpent).append(" ms\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }
}
