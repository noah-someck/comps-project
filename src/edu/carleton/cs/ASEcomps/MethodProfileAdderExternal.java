package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class MethodProfileAdderExternal extends MethodVisitor implements LVSUser {
    private String name;
    private boolean isMain;
    private int time = -1;
    private int addedStack = 0;
    private int addedLocals = 0;
    private boolean encounteredExit = false;
    private LocalVariablesSorter lvs;

    public MethodProfileAdderExternal(int api, String name) {
        super(api);
        this.name = name;
    }

    public MethodProfileAdderExternal(int api, MethodVisitor mv, String name) {
        super(api, mv);
        this.name = name;
    }

    public MethodProfileAdderExternal(int api, MethodVisitor mv, int access, String name, String descriptor, String signature, String[] exceptions) {
        this(api, mv, name);
        isMain = (name.endsWith("Main") && descriptor.contentEquals("([Ljava/lang/String;)V")); // incomplete
        System.out.println(isMain);
    }

    public void setLVS(LocalVariablesSorter lvs) {
        this.lvs = lvs;
    }

    @Override
    public void visitCode() {
        if (lvs == null) {
            throw new NullPointerException("This instance's LocalVariablesSorter must be set before use");
        }
        super.visitCode(); // Get to start of instruction segment
        super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        time = lvs.newLocal(Type.LONG_TYPE);
        super.visitVarInsn(Opcodes.LSTORE, time);
        addedStack = Math.max(addedStack, 2);
        //addedLocals += 2; // unnecessary because the lvs takes care of this local.
    }

    @Override
    public void visitInsn(int opcode) {
        // Before the instruction opcode would have otherwise been visited:
        if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
            super.visitLdcInsn(name);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
            super.visitVarInsn(Opcodes.LLOAD, time);
            super.visitInsn(Opcodes.LSUB);
            super.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/carleton/cs/ASEcomps/ExternalProfileAccumulator", "recordMethodUse", "(Ljava/lang/String;J)V", false);
            if (!encounteredExit) {
                encounteredExit = true;
                addedStack = Math.max(addedStack, 5);
            }
            if (isMain) {
                super.visitFieldInsn(Opcodes.GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream;");
                super.visitMethodInsn(Opcodes.INVOKESTATIC, "edu/carleton/cs/ASEcomps/ExternalProfileAccumulator", "getReport", "()Ljava/lang/String;", false);
                super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);
            }
        }
        // Finally, visit instruction opcode
        super.visitInsn(opcode);

    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + addedStack, maxLocals + addedLocals);
        addedStack = 0;
        addedLocals = 0;
        encounteredExit = false;
    }
}