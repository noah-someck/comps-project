package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
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
                super.visitFieldInsn(opcode, owner, name, descriptor);
                if ((opcode == Opcodes.GETSTATIC || opcode == Opcodes.GETFIELD) && descriptor.equals("Ljava/lang/String;")) {

                    super.visitInsn(Opcodes.DUP); // put the string on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringSearchHolder", "checkStringSearch", "(Ljava/lang/String;)Z", false);

                    super.visitInsn(Opcodes.POP); // pop the boolean returned off stack
                }
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                if (descriptor.endsWith(")Ljava/lang/String;")) {

                    super.visitInsn(Opcodes.DUP); // put the string on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringSearchHolder", "checkStringSearch", "(Ljava/lang/String;)Z", false);

                    super.visitInsn(Opcodes.POP); // pop the boolean returned off stack
                }
            }

            @Override
            public void visitLdcInsn(Object value) {
                super.visitLdcInsn(value);
                if (value instanceof String) {
                    super.visitInsn(Opcodes.DUP); // put the string on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringSearchHolder", "checkStringSearch", "(Ljava/lang/String;)Z", false);

                    super.visitInsn(Opcodes.POP); // pop the boolean returned off stack
                }
            }

            @Override
            public void visitInsn(int opcode) {
                super.visitInsn(opcode);
                if (opcode == Opcodes.AALOAD) {
                    super.visitInsn(Opcodes.DUP); // put the string on stack
                    super.visitTypeInsn(Opcodes.INSTANCEOF, "Ljava/lang/String;");
                    Label compareLabel = new Label();
                    super.visitJumpInsn(Opcodes.IFEQ, compareLabel);

                    // BEGIN this if object loaded from array is a String
                    super.visitInsn(Opcodes.DUP); // put the string on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringSearchHolder", "checkStringSearch", "(Ljava/lang/String;)Z", false);

                    super.visitInsn(Opcodes.POP); // pop the boolean returned off stack
                    // END

                    super.visitLabel(compareLabel);
                }
            }

            // Need to add checks after accessing an element of an array of Strings, and after casting an object
            // to String. Could also do for LDC instructions that load Strings.
        };
        return methodStringReturnCheckAdder;
    }



}
