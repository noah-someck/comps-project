package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.MethodVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Constructor;
import java.util.function.Predicate;

public class Agent {
    public static void premain(String premainArgs, Instrumentation inst) {
        ClassFileTransformer addTimers = new AddTimerEachMethod();
        Predicate<String> startsWithEdu = (s) -> s.startsWith("edu/");
        inst.addTransformer(ClassFileTransformers.FilterByClassName(addTimers, startsWithEdu));
//        Constructor<MethodProfileAdder> constructor;
//        try {
//            constructor = MethodProfileAdder.class.getConstructor(int.class, MethodVisitor.class, int.class, String.class, String.class, String.class, String[].class);
//        } catch (NoSuchMethodException e) {
//            e.printStackTrace();
//            return;
//        }
//        ClassFileTransformer profiler = ClassFileTransformers.ApplyLVSMethodVisitor(constructor);
//        inst.addTransformer(ClassFileTransformers.FilterByClassName(profiler, startsWithEdu));
//        inst.addTransformer(ClassFileTransformers.FilterByClassName(ClassFileTransformers.ClassPrinter, startsWithEdu));
    }
}
