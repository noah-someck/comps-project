package edu.carleton.cs.ASEcomps;

import com.sun.istack.internal.NotNull;
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
    public static ClassFileTransformer FilterByClassName(@NotNull ClassFileTransformer transformer, Predicate<String> stringPredicate) {
        return (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) -> {
            if (stringPredicate.test(className))
                return transformer.transform(loader, className, classBeingRedefined, protectionDomain, classfileBuffer);
            return classfileBuffer;
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
                return classfileBuffer;
            };


    public static ClassFileTransformer ApplyLVSMethodVisitor(Constructor<?> methodVisitorConstructor) {
        return (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) ->
            {
                ClassReader cr = new ClassReader(classfileBuffer);
                ClassWriter cw = new ClassWriter(cr, 0);
                ClassVisitor useLVSMethodVisitor = new ClassVisitor(Opcodes.ASM6, cw) {
                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
                        LVSUser preLVSMv = null;
                        try {
                            preLVSMv = (LVSUser) methodVisitorConstructor.newInstance(api, mv, access, name, descriptor, signature, exceptions);
                        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
                            e.printStackTrace();
                            System.exit(1);
                        }
                        LocalVariablesSorter lvs = new LocalVariablesSorter(access, descriptor, preLVSMv);
                        preLVSMv.setLVS(lvs);
                        return lvs;
                    }
                };
                try {
                    cr.accept(useLVSMethodVisitor, ClassReader.EXPAND_FRAMES);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
                return cw.toByteArray();
            };
    }
}
