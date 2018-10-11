package edu.carleton.cs.ASEcomps;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ProfileAccumulator {
    static Map<String, Long> methodTimes = new ConcurrentHashMap<>();
    static Map<String, Integer> methodCalls = new ConcurrentHashMap<>();

    public static void recordMethodUse(String methodName, long timeInMethod) {
        methodTimes.merge(methodName, timeInMethod, Long::sum);
        methodCalls.merge(methodName, 1, Integer::sum);
//        if (!methodTimes.containsKey(methodName)) {
//            assert !methodCalls.containsKey(methodName);
//            methodTimes.put(methodName, timeInMethod);
//            methodCalls.put(methodName, 1);
//        } else {
//            Long prevTime = methodTimes.get(methodName);
//            methodCalls.replace(methodName, methodCalls.get(methodName) + 1);
//        }
    }

    public static String getReport() {
        StringBuilder sb = new StringBuilder("Method Usage Summary:\n");
        for (String methodName : methodTimes.keySet()) {
            sb.append(methodName).append(" was called ").append(methodCalls.get(methodName)).append(" times.\n");
            sb.append("    Total time spent: ").append(methodTimes.get(methodName)).append("\n");
        }
        return sb.toString();
    }
}
