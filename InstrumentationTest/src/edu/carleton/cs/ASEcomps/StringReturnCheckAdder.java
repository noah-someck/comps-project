package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.*;
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
                // Covers Strings gotten from instance or class fields:
                if ((opcode == Opcodes.GETSTATIC || opcode == Opcodes.GETFIELD) && descriptor.equals("Ljava/lang/String;")) {

                    super.visitInsn(Opcodes.DUP); // put the string on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringSearchHolder", "checkStringSearch", "(Ljava/lang/String;)Z", false);

                    super.visitInsn(Opcodes.POP); // pop the boolean returned off stack
                }
            }

            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                if (descriptor.endsWith(")Ljava/lang/String;")) { // Covers method invocations which return String

                    super.visitInsn(Opcodes.DUP); // put the string on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringSearchHolder", "checkStringSearch", "(Ljava/lang/String;)Z", false);

                    super.visitInsn(Opcodes.POP); // pop the boolean returned off stack
                }
            }

            @Override
            public void visitLdcInsn(Object value) {
                super.visitLdcInsn(value);
                if (value instanceof String) { // Covers loads from the constant pool
                    super.visitInsn(Opcodes.DUP); // put the string on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringSearchHolder", "checkStringSearch", "(Ljava/lang/String;)Z", false);

                    super.visitInsn(Opcodes.POP); // pop the boolean returned off stack
                }
            }

            @Override
            public void visitInsn(int opcode) {
                super.visitInsn(opcode);
                if (opcode == Opcodes.AALOAD) { // Covers Strings loaded from array
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

            @Override
            public void visitTypeInsn(int opcode, String type) {
                super.visitTypeInsn(opcode, type);
                if (opcode == Opcodes.CHECKCAST && type.equals("java/lang/String")) { // Cover (in most? cases) Strings that were cast from another class
                    super.visitInsn(Opcodes.DUP); // put the string on stack

                    super.visitMethodInsn(Opcodes.INVOKESTATIC,"edu/carleton/cs/ASEcomps/StringSearchHolder", "checkStringSearch", "(Ljava/lang/String;)Z", false);

                    super.visitInsn(Opcodes.POP); // pop the boolean returned off stack

                    super.visitInsn(202);
                }
            }
        };
        return methodStringReturnCheckAdder;
    }



}
