package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

import java.util.function.Supplier;

public class ExternalProfilerAdder extends ClassVisitor {
    private boolean skipClass;

    public ExternalProfilerAdder(int api, ClassVisitor classVisitor, String className) {
        super(api, classVisitor);
        skipClass = className.contentEquals(ExternalProfileAccumulator.class.getName());
    }



    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        if (skipClass) return super.visitMethod(access, name, descriptor, signature, exceptions);
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        PrintResultsInMain mainVisitor = new PrintResultsInMain(api, mv, access, name, descriptor);
        MethodRecordTimeExternal methodRecordTimeExternal = new MethodRecordTimeExternal(api, mainVisitor, name);
        MethodTimerAdder methodTimerAdder = new MethodTimerAdder(api, methodRecordTimeExternal, access, name, descriptor);
        methodRecordTimeExternal.setTimeSupplier(methodTimerAdder::getTime);
        return LVSUser.buildChain(access, descriptor, mv, mainVisitor, methodRecordTimeExternal, methodTimerAdder);
    }

    public static class MethodRecordTimeExternal extends MethodVisitor {
        private final String name;
        private Supplier<Integer> timeSupplier;

        MethodRecordTimeExternal(int api, MethodVisitor methodVisitor, String name) {
            super(api, methodVisitor);
            this.name = name;
        }

        void setTimeSupplier(Supplier<Integer> supplier) {
            this.timeSupplier = supplier;
        }

        @Override
        public void visitInsn(int opcode) {
            if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
                super.visitLdcInsn(name);
                super.visitVarInsn(Opcodes.LLOAD, timeSupplier.get());
                super.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/carleton/cs/ASEcomps/ExternalProfileAccumulator", "recordMethodUse", "(Ljava/lang/String;J)V", false);
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 3, maxLocals);
        }
    }

    public static class PrintResultsInMain extends MethodVisitor {
        private boolean isMain;

        public PrintResultsInMain(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
            super(api, methodVisitor);
            isMain = name.contentEquals("main") && descriptor.contentEquals("([Ljava/lang/String;)V");
            isMain = isMain && (access & Opcodes.ACC_PUBLIC) > 0 && (access & Opcodes.ACC_STATIC) > 0;
        }

        @Override
        public void visitInsn(int opcode) {
            if (isMain && (opcode == Opcodes.RETURN || opcode == Opcodes.ATHROW)) {
                super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/carleton/cs/ASEcomps/ExternalProfileAccumulator", "getReport", "()Ljava/lang/String;", false);
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            int addedStack = isMain ? 2 : 0;
            super.visitMaxs(maxStack + addedStack, maxLocals);
        }
    }
}
