package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class StringReturnCheckAdder extends ClassVisitor {
    public StringReturnCheckAdder(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor baseMv = super.visitMethod(access, name, descriptor, signature, exceptions);
        MethodVisitor methodStringReturnCheckAdder = new MethodVisitor(Opcodes.ASM6, baseMv) {
            @Override
            public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {

                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        };
        return methodStringReturnCheckAdder;
    }



}
