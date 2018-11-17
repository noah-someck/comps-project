package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.LocalVariablesSorter;

import java.util.function.Supplier;

public class PrintingTimerAdder extends ClassVisitor {
    public PrintingTimerAdder(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        MethodTimePrinter methodTimePrinter = new MethodTimePrinter(api, mv);
        MethodTimerAdder methodTimerAdder = new MethodTimerAdder(api, methodTimePrinter, access, name, descriptor);
        methodTimePrinter.setTimeSupplier(methodTimerAdder::getTime);
        return LVSUser.buildChain(access, descriptor, mv, methodTimePrinter, methodTimerAdder);
    }

    public static class MethodTimePrinter extends MethodVisitor {
        private Supplier<Integer> timeSupplier;

        public MethodTimePrinter(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        public void setTimeSupplier(Supplier<Integer> supplier) {
            this.timeSupplier = supplier;
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
                super.visitVarInsn(Opcodes.LLOAD, timeSupplier.get());
                super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/Long", "toString", "(J)Ljava/lang/String;", false);
                super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitInsn(Opcodes.SWAP);
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
            super.visitInsn(opcode);
        }
    }
}
