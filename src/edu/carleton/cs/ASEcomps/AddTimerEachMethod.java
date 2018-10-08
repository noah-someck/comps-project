package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;


public class AddTimerEachMethod implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (!className.contains("carleton") || !className.contains("Main")) {
            return classfileBuffer;
        }
        System.out.println("AddTimerEachMethod transform started");
        ClassReader cr = new ClassReader(classfileBuffer);
        ClassWriter cw = new ClassWriter(cr, 0);
        Textifier textifier = new Textifier();
        ClassVisitor timerAddVisitor = new ClassVisitor(Opcodes.ASM6, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor traceMethodVisitor = new TraceMethodVisitor(textifier);
                MethodTimerAdder methodTimerAdder = new MethodTimerAdder(Opcodes.ASM6, traceMethodVisitor);
                LocalVariablesSorter lvs = new LocalVariablesSorter(access, descriptor, methodTimerAdder);
                methodTimerAdder.setLVS(lvs);
                return lvs;
            }
        };
        PrintWriter pw = new PrintWriter(System.out);
        ClassVisitor traceClassVisitor = new TraceClassVisitor(pw);
        try {
            cr.accept(timerAddVisitor, ClassReader.EXPAND_FRAMES);
            cr.accept(cw, 0);
//            cr.accept(traceClassVisitor, 0);
            System.out.println(textifier.text);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        System.out.println("AddTimerEachMethod transform ran");
        return cw.toByteArray();
        //return classfileBuffer;
    }
}
