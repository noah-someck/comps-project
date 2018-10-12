package edu.carleton.cs.ASEcomps;

import com.sun.istack.internal.NotNull;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

public interface LVSUser {
    public void setLVS(LocalVariablesSorter lvs);

    public static LocalVariablesSorter buildChain(int access, @NotNull String descriptor, MethodVisitor ... methodVisitors) {
        MethodVisitor lastMv = methodVisitors[methodVisitors.length - 1];
        LocalVariablesSorter lvs = new LocalVariablesSorter(access, descriptor, lastMv);
        for (MethodVisitor mv : methodVisitors) {
            if (mv instanceof LVSUser) {
                ((LVSUser) mv).setLVS(lvs);
            }
        }
        return lvs;
    }
}
