package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;
import org.objectweb.asm.commons.LocalVariablesSorter;

public class MethodTimerAdder extends AdviceAdapter implements LVSUser {
    private int time = -1;
    private int addedStack = 0;
    private int addedLocals = 0;
    private boolean encounteredExit = false;
    private LocalVariablesSorter lvs;

    MethodTimerAdder(int api, MethodVisitor methodVisitor, int access, String name, String descriptor) {
        super(api, methodVisitor, access, name, descriptor);
    }

    public void setLVS(LocalVariablesSorter lvs) {
        this.lvs = lvs;
    }

    public int getTime() {return time;}

    @Override
    public void onMethodEnter() {
        if (lvs == null) {
            throw new NullPointerException("This instance's LocalVariablesSorter must be set before use");
        }
        super.onMethodEnter(); // Get to start of instruction segment, or just after call to super in a constructor
        super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        time = lvs.newLocal(Type.LONG_TYPE);
        super.visitVarInsn(LSTORE, time);
        addedStack = Math.max(addedStack, 2);
    }

    @Override
    public void onMethodExit(int opcode) {
        // Before the method exits
        super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "currentTimeMillis", "()J", false);
        super.visitVarInsn(LLOAD, time);
        super.visitInsn(LSUB);
        super.visitVarInsn(Opcodes.LSTORE, time);
        if (!encounteredExit) {
            encounteredExit = true;
            addedStack = Math.max(addedStack, 4);
        }
        super.onMethodExit(opcode);


    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        super.visitMaxs(maxStack + addedStack, maxLocals + addedLocals);
        addedStack = 0;
        addedLocals = 0;
        encounteredExit = false;
    }
}
