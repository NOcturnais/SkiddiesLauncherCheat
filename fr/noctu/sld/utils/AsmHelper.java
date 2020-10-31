package fr.noctu.sld.utils;

import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

public class AsmHelper {
    public static MethodNode findMethod(String name, ClassNode classNode){
        for(MethodNode methodNode : classNode.methods){
            if(name.equals(methodNode.name))
                return methodNode;
        }
        return null;
    }
}
