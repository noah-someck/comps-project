package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.function.Predicate;

class ClassPrinter implements ClassFileTransformer {

    /**
     * Prints a text representation of the given class's bytecode.
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader cr = new ClassReader(classfileBuffer);
        PrintWriter pw = new PrintWriter(System.out);
        ClassVisitor cv = new TraceClassVisitor(null, new Textifier(), pw);
        cr.accept(cv, 0);
        return classfileBuffer;
    }
}

public class Agent {
    public static void premain(String premainArgs, Instrumentation inst) {
        ClassFileTransformer addTimers = new AddTimerEachMethod();
        Predicate<String> startsWithEdu = (s) -> s.startsWith("edu/");
        inst.addTransformer(ClassFileTransformers.FilterByClassName(addTimers, startsWithEdu));

        inst.addTransformer(new ClassPrinter());
    }
}
