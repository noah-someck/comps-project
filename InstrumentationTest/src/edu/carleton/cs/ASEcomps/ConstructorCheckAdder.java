package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.AdviceAdapter;

public class ConstructorCheckAdder extends ClassVisitor {
    public ConstructorCheckAdder(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor baseMv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (!name.equals("<init>")) return baseMv;
        MethodVisitor methodVisitor = new AdviceAdapter(Opcodes.ASM6, baseMv, access, name, descriptor) {
            @Override
            protected void onMethodExit(int opcode) {
                if (opcode != Opcodes.ATHROW) {
                    super.visitInsn(Opcodes.DUP);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/carleton/cs/ASEcomps/StringChecker", "check", "(Ljava/lang/String;)V", false);
                } super.onMethodExit(opcode);
            }
        };
        return methodVisitor;
    }

}
