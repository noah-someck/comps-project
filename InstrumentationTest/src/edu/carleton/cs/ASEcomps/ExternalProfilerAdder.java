package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.util.Textifier;

import java.lang.reflect.Modifier;
import java.util.function.Supplier;

public class ExternalProfilerAdder extends ClassVisitor {
    private boolean skipClass = false;
//    private String className;

    public ExternalProfilerAdder(int api, ClassVisitor classVisitor, String className) {
        super(api, classVisitor);
        // Note: attempting to use ExternalProfileAccumulator before the system classloader starts (e.g. transformation
        // of classes loaded by bootstrap classloader) will fail with cryptic or absent error messages
        skipClass = className == null || className.equals(ExternalProfileAccumulator.class.getName());
//        this.className = className;
    }


    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        if (skipClass) return mv;

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
            isMain = name.equals("main") && descriptor.equals("([Ljava/lang/String;)V");
            isMain = isMain && Modifier.isStatic(access) && Modifier.isPublic(access);
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
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            // This inserts the get/print code right before any invocation of (java/lang/)System.exit, which covers
            // many (but not all) cases where a Java program ends normally but does not reach RETURN or ATHROW in main
            if (opcode == Opcodes.INVOKESTATIC && owner.equals("java/lang/System") && name.equals("exit")) {
                super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/carleton/cs/ASEcomps/ExternalProfileAccumulator", "getReport", "()Ljava/lang/String;", false);
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
            super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            int addedStack = isMain ? 2 : 0;
            super.visitMaxs(maxStack + addedStack, maxLocals);
        }
    }
}
