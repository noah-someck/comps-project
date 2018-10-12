package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public interface ClassVisitors {
    public static ClassVisitor fromMethodVisitor(ClassVisitor parent, MethodVisitor methodVisitor) {
        return new ClassVisitor(Opcodes.ASM6, parent) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                //super.visitMethod(access, name, descriptor, signature, exceptions);
                return methodVisitor;
            }
        };
    }

}
