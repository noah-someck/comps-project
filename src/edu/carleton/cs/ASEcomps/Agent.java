package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.util.function.Predicate;



public class Agent {
    public static void premain(String premainArgs, Instrumentation inst) {
        Predicate<String> startsWithEdu = (s) -> s.startsWith("edu/");
//        ClassFileTransformer transformer = ClassFileTransformers.fromClassVisitor(PrintingTimerAdder.class, Opcodes.ASM6, 0, ClassReader.EXPAND_FRAMES);
        ClassFileTransformer transformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) ->
        {
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, 0);
            try {ExternalProfilerAdder cv = new ExternalProfilerAdder(Opcodes.ASM6, cw, className);

                cr.accept(cv, ClassReader.EXPAND_FRAMES);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return cw.toByteArray();
        };

        inst.addTransformer(ClassFileTransformers.filterByClassName(transformer, startsWithEdu));
//        inst.addTransformer(ClassFileTransformers.filterByClassName(ClassFileTransformers.ClassPrinter, startsWithEdu));
    }
}
