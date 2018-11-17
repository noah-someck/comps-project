package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;
import java.util.function.Predicate;


public class Agent {
    public static void premain(String premainArgs, Instrumentation inst) {
        //inst.addTransformer(testTransformer());
        Predicate<String> startsWithEdu = (s) -> s.startsWith("edu/");
        Predicate<String> notJava = (s) -> !s.startsWith("java/");
        Predicate<String> stringClass = (s) -> s.contains("java/lang");
        // Note to self: check out ModuleVisitor.visitMainClass()
//        ClassFileTransformer add_printing_timer = ClassFileTransformers.fromClassVisitor(PrintingTimerAdder.class, Opcodes.ASM6, 0, ClassReader.EXPAND_FRAMES);
//        inst.addTransformer(ClassFileTransformers.skipBootstrapped(add_printing_timer));

//        ClassFileTransformer externalProfilingTransformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) ->
//        {
//            ClassReader cr = new ClassReader(classfileBuffer);
//            ClassWriter cw = new ClassWriter(cr, 0);
//            try {
//                ExternalProfilerAdder cv = new ExternalProfilerAdder(Opcodes.ASM6, cw, className);
//                cr.accept(cv, ClassReader.EXPAND_FRAMES);
//            } catch (Throwable t) {
//                t.printStackTrace();
//            }
//            return cw.toByteArray();
//        };

        ClassFileTransformer stringConstructorCheckTransformer = (loader, className, classBeingRedefined, protectionDomain, classfileBuffer) ->
        {
//            System.out.println("Transforming " + className + "..." + (loader == null ? " (bootstrap)" : loader.toString()));
            if (className.equals("edu/carleton/cs/ASEcomps/StringChecker")) {return null;}
            if (className.equals("edu/carleton/cs/ASEcomps/StringSearchHolder")) {return null;}
            ClassReader cr = new ClassReader(classfileBuffer);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES);
            try {
//                StringReturnCheckAdderV1 cv1 = new StringReturnCheckAdderV1(Opcodes.ASM6, cw, className);
//                cr.accept(cv1, ClassReader.EXPAND_FRAMES);
                StringReturnCheckAdder cv = new StringReturnCheckAdder(Opcodes.ASM6, cw, className);
                cr.accept(cv, ClassReader.EXPAND_FRAMES);
            } catch (Throwable t) {
                t.printStackTrace();
            }
            return cw.toByteArray();
        };
        inst.addTransformer(ClassFileTransformers.skipBootstrapped(stringConstructorCheckTransformer));
//        try {
//            inst.retransformClasses(StringChecker.class, String.class);
//        } catch (UnmodifiableClassException e) {
//            e.printStackTrace();
//        }


//        inst.addTransformer(ClassFileTransformers.skipBootstrapped(externalProfilingTransformer));
//        inst.addTransformer(ClassFileTransformers.filterByClassName(ClassFileTransformers.ClassPrinter, startsWithEdu));
    }


    private static ClassFileTransformer testTransformer() {
        return new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
//                if (className == null || !className.equals("java/lang/System")) {
                if (!className.equals("java/lang/Shutdown")) {
                    return null;
                }
                ClassReader cr = new ClassReader(classfileBuffer);
                Printer printer = new Textifier();
                ClassVisitor classVisitor = new ClassVisitor(Opcodes.ASM6) {
                    @Override
                    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                        printer.visit(version, access, name, signature, superName, interfaces);
                    }

                    @Override
                    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                        if (name.equals("halt") && Modifier.isStatic(access))
                            printer.visitMethod(access, name, descriptor, signature, exceptions);

                        return super.visitMethod(access, name, descriptor, signature, exceptions);

                    }
                };
                cr.accept(classVisitor, 0);
                printer.print(new PrintWriter(System.out));
                System.out.println(printer.text);
                return null;
            }
        };
    }
}
