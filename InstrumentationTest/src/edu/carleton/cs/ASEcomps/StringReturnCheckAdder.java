package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

public class StringReturnCheckAdder extends ClassVisitor {
    private final String className;

    public StringReturnCheckAdder(int api, ClassVisitor classVisitor, String className) {
        super(api, classVisitor);
        this.className = className;
    }

    @Override
    public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
        MethodVisitor baseMv = super.visitMethod(access, methodName, descriptor, signature, exceptions);
        MethodVisitor methodStringReturnCheckAdder = new MethodVisitor(Opcodes.ASM6, baseMv) {
            @Override
            public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
                if ((opcode == Opcodes.GETSTATIC || opcode == Opcodes.GETFIELD) && descriptor.equals("Ljava/lang/String;")) {
                    Textifier textifier = new Textifier();
                    MethodVisitor tracer = new TraceMethodVisitor(mv, textifier);
                    tracer.visitFieldInsn(opcode, owner, name, descriptor); // takes care of super.visitFieldInsn

                    super.visitInsn(Opcodes.DUP); // put the string on stack
                    super.visitLdcInsn(className + "." + methodName); // put full method name on stack
                    super.visitLdcInsn(textifier.text.get(0)); // put text of bytecode instruction on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringChecker", "check", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
                } else {
                    super.visitFieldInsn(opcode, owner, name, descriptor);
                }
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                if (descriptor.endsWith(")Ljava/lang/String;")) {
                    Textifier textifier = new Textifier();
                    MethodVisitor tracer = new TraceMethodVisitor(mv, textifier);
                    tracer.visitMethodInsn(opcode, owner, name, descriptor, isInterface); // takes care of super.visitMethodInsn

                    super.visitInsn(Opcodes.DUP); // put the string on stack
                    super.visitLdcInsn(className + "." + methodName); // put full method name on stack
                    super.visitLdcInsn(textifier.text.get(0)); // put text of bytecode instruction on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringChecker", "check", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
                } else {
                    super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                }
            }

            // Need to add checks after accessing an element of an array of Strings, and after casting an object
            // to String. Could also do for LDC instructions that load Strings.
        };
        return methodStringReturnCheckAdder;
    }



}
