package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;
import org.objectweb.asm.util.ASMifier;

import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Method;

public class MethodTimerAdder extends MethodVisitor {
    private int time;
    private LocalVariablesSorter lvs;

    public MethodTimerAdder(int api) {
        super(api);
    }

    public MethodTimerAdder(int api, MethodVisitor mv) {
        super(api, mv);
    }

    public void setLVS(LocalVariablesSorter lvs) {
        this.lvs = lvs;
    }

    @Override
    public void visitCode() {
        super.visitCode(); // Get to start of instruction segment
        try {
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            time = lvs.newLocal(Type.LONG_TYPE);
            super.visitVarInsn(Opcodes.LSTORE, time);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void visitInsn(int opcode) {
        System.out.println(ASMifier.OPCODES[opcode]);
        // Before the instruction opcode would have otherwise been visited:
        try {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
                System.out.println("found end");
                super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
                super.visitVarInsn(Opcodes.LLOAD, time);
                super.visitInsn(Opcodes.LSUB);
                super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "toString", "(J)Ljava/lang/String;", false);
                super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(Opcodes.SWAP);
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
//                visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
//                visitVarInsn(Opcodes.LLOAD, time);
//                visitInsn(Opcodes.LSUB);
//                visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "toString", "(J)Ljava/lang/String;", false);
//                visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
//                visitInsn(Opcodes.SWAP);
//                visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

            }
            // Finally, visit instruction opcode
            super.visitInsn(opcode);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + 4, maxLocals + 2);
        System.out.println("visitMaxs");
    }
}
