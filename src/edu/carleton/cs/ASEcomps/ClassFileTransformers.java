package edu.carleton.cs.ASEcomps;

import com.sun.istack.internal.NotNull;

import java.lang.instrument.ClassFileTransformer;
import java.util.function.Predicate;

public interface ClassFileTransformers {
    /**
     * @param transformer     Any non-null ClassFileTransformer
     * @param stringPredicate A Predicate on Strings that returns true for class names that should be transformed.
     * @return A ClassFileTransformer that applies given transformer on a class if the class's name satisfies the given
     * predicate, and otherwise passes the class through unchanged.
     */
    public static ClassFileTransformer FilterByClassName(@NotNull ClassFileTransformer transformer, Predicate<String> stringPredicate) {
        return (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (stringPredicate.test(className))
                return transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            return classfileBuffer;
        };
    }
}
