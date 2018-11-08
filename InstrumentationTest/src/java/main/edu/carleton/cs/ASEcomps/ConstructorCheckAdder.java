package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class ConstructorCheckAdder extends ClassVisitor {
    public ConstructorCheckAdder(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor baseMv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (!name.equals("<init>") || descriptor.equals("([BLjava/lang/String;)V") || descriptor.equals("([BLjava/nio/Charset;)V") || descriptor.equals("([B)V") || descriptor.equals("([BI)V")) return baseMv;
        MethodVisitor methodVisitor = new AdviceAdapter(Opcodes.ASM6, baseMv, access, name, descriptor) {
            @Override
            protected void onMethodExit(int opcode) {
                if (opcode != Opcodes.ATHROW) {
                    super.visitFieldInsn(Opcodes.GETFIELD, Type.getInternalName(String.class), "value", "[C");
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/carleton/cs/ASEcomps/StringChecker", "check", "([C)V", false);
                } super.onMethodExit(opcode);
            }
        };
        return methodVisitor;
    }

}
