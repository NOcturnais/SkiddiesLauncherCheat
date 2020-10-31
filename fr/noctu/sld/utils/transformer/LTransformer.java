package fr.noctu.sld.utils.transformer;

import jdk.internal.org.objectweb.asm.tree.ClassNode;

public interface LTransformer {
    public byte[] visitClass(String className, ClassNode classNode);
}
