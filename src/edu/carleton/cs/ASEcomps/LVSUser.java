package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.LocalVariablesSorter;

public abstract class LVSUser extends MethodVisitor {
    public LVSUser(int api) {
        super(api);
    }

    public LVSUser(int api, MethodVisitor methodVisitor) {
        super(api, methodVisitor);
    }

    public LVSUser(int api, MethodVisitor methodVisitor, int access, String name, String descriptor, String signature, String[] exceptions) {
        super(api, methodVisitor);
    }

    public abstract void setLVS(LocalVariablesSorter lvs);



}
