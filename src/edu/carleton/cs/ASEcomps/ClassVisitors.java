package edu.carleton.cs.ASEcomps;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface ClassVisitors {
    public static ClassVisitor fromMethodVisitor(ClassVisitor cv, Class<? extends MethodVisitor> methodVisitorClass, int api) throws NoSuchMethodException {
        Constructor<? extends MethodVisitor> methodVisitorConstructor;
        try {
            methodVisitorConstructor = methodVisitorClass.getConstructor(int.class, MethodVisitor.class);
        } catch (NoSuchMethodException e) {
            try {
                methodVisitorConstructor = methodVisitorClass.getConstructor(int.class, MethodVisitor.class, int.class, String.class, String.class, String.class, String[].class);
            } catch (NoSuchMethodException e2) {
                System.err.println("The MethodVisitor Class given must have a constructor with parameters (int, MethodVisitor)");
                throw e2;
            }
        }

        Constructor<? extends MethodVisitor> finalMethodVisitorConstructor = methodVisitorConstructor;
        return new ClassVisitor(Opcodes.ASM6, cv) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
                MethodVisitor mv = null;
                if (finalMethodVisitorConstructor.getParameterCount() == 2) {
                    try {
                        mv = finalMethodVisitorConstructor.newInstance(api, super.visitMethod(access, name, descriptor, signature, exceptions));
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                } else {
                    try {
                        mv = finalMethodVisitorConstructor.newInstance(api, super.visitMethod(access, name, descriptor, signature, exceptions), access, name, descriptor, signature, exceptions);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace(System.err);
                        System.exit(1);
                    }
                }
                return mv;
            }
        };
    }

}
