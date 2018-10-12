package edu.carleton.cs.ASEcomps;

import jdk.internal.org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.function.Predicate;


class AddTimerVisitor extends ClassVisitor {
    public AddTimerVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        MethodAddPrintingTimer methodTimerAdder = new MethodAddPrintingTimer(Opcodes.ASM6, mv);
        return LVSUser.buildChain(access, descriptor, mv, methodTimerAdder);
    }
};

public class Agent {
    public static void premain(String premainArgs, Instrumentation inst) {
        ClassFileTransformer addTimers = new AddTimerEachMethod();
        Predicate<String> startsWithEdu = (s) -> s.startsWith("edu/");
//        inst.addTransformer(ClassFileTransformers.filterByClassName(addTimers, startsWithEdu));
        ClassFileTransformer transformer = ClassFileTransformers.fromVisitor(AddTimerVisitor.class, Opcodes.ASM6, 0, ClassReader.EXPAND_FRAMES);
        inst.addTransformer(ClassFileTransformers.filterByClassName(transformer, startsWithEdu));
    }
}
