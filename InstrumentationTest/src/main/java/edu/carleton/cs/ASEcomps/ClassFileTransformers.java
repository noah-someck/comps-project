package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Predicate;

public interface ClassFileTransformers {
    /**
     * @param transformer     Any non-null ClassFileTransformer
     * @param stringPredicate A Predicate on Strings that returns true for class names that should be transformed.
     * @return A ClassFileTransformer that applies given transformer on a class if the class's name satisfies the given
     * predicate, and otherwise passes the class through unchanged.
     */
    public static ClassFileTransformer filterByClassName(ClassFileTransformer transformer, Predicate<String> stringPredicate) {
        return (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (stringPredicate.test(className))
                return transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            return null;
        };
    }

    /**
     * Just prints a text representation of a class's bytecode (using Textifier) to System.out
     */
    public static ClassFileTransformer ClassPrinter =
            (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) ->
            {
                ClassReader cr = new ClassReader(classfileBuffer);
                PrintWriter pw = new PrintWriter(System.out);
                ClassVisitor cv = new TraceClassVisitor(null, new Textifier(), pw);
                cr.accept(cv, 0);
                return null;
            };

    public static ClassFileTransformer fromClassVisitor(Class<? extends ClassVisitor> visitorClass, int api, int writerFlags, int parsingOptions) {
        return (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) ->
        {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, writerFlags);
            Constructor<? extends ClassVisitor> visitorConstructor;
            ClassVisitor cv;
            try {
                visitorConstructor = visitorClass.getConstructor(int.class, ClassVisitor.class);
            } catch (NoSuchMethodException e) {
                System.out.println("The ClassVisitor Class given must have a constructor with parameters (int, ClassVisitor)");
                e.printStackTrace();
                visitorConstructor = null;
            }
            assert visitorConstructor != null;
            try {
                cv = visitorConstructor.newInstance(api, cw);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
                cv = null;
            }
            assert cv != null : "Instantiation of the ClassVisitor failed.";
            try {
                cr.accept(cv, parsingOptions);
            } catch (Exception t) {
                t.printStackTrace();
            }
            return cw.toByteArray();
        };
    }

    /**
     * Most of the time, we don't want to transform the classes that are loaded by the bootstrap classloader
     * @param transformer
     * @return
     */
    public static ClassFileTransformer skipBootstrapped(ClassFileTransformer transformer) {
        return (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (loader == null) return null;
            return transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
        };
    }

}
