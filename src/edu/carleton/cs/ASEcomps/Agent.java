package edu.carleton.cs.ASEcomps;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

class ClassPrinter implements ClassFileTransformer {

    /**
     * Checks if the class name has "carleton" in it, and if it does, prints the bytecode.
     * @param loader
     * @param className
     * @param classBeingRedefined
     * @param protectionDomain
     * @param classfileBuffer
     * @return
     * @throws IllegalClassFormatException
     */
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.contains("carleton")) {
            return classfileBuffer;
        }
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, 0);
        PrintWriter pw = new PrintWriter(System.out);
        ClassVisitor cv = new TraceClassVisitor(pw);
        cr.accept(cv, 0);
        cr.accept(cw, 0);
        return cw.toByteArray();
    }
}

public class Agent {
    public static void premain(String premainArgs, Instrumentation inst){
        inst.addTransformer(new AddTimerEachMethod());
//        inst.addTransformer(new ClassPrinter());
    }
}
