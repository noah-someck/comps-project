package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.*;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

@SuppressWarnings("Duplicates")
@Deprecated /* Kept code because it can be informative to see whole transformer in one place. Not best use though. */
public class AddTimerEachMethod implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassReader cr = new ClassReader(classfileBuffer);
//        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
        ClassWriter cw = new ClassWriter(cr, 0);
        Textifier textifier = new Textifier();
        ClassVisitor timerAddVisitor = new ClassVisitor(Opcodes.ASM6, cw) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
//                MethodVisitor traceMethodVisitor = new TraceMethodVisitor(mv, textifier);
//                MethodAugmenter methodTimerAdder = new MethodAugmenter(Opcodes.ASM6, traceMethodVisitor);
                MethodTimerAdder methodTimerAdder = new MethodTimerAdder(Opcodes.ASM6, mv, access, name, descriptor);
                LocalVariablesSorter lvs = new LocalVariablesSorter(access, descriptor, methodTimerAdder);
                methodTimerAdder.setLVS(lvs);
                return lvs;
            }
        };
        PrintWriter pw = new PrintWriter(System.out);
        ClassVisitor traceClassVisitor = new TraceClassVisitor(pw);
        try {
            cr.accept(timerAddVisitor, ClassReader.EXPAND_FRAMES);
            //cr.accept(cw, 0);
//            System.out.println("cw contents:");
//            (new ClassReader(cw.toByteArray())).accept(traceClassVisitor, 0);
            // The following line just prints the original class because cr doesn't get modified.
            //cr.accept(traceClassVisitor, 0);

            //System.out.println("textifier output:\n" + textifier.text);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return cw.toByteArray();
        //return null;
    }
}
