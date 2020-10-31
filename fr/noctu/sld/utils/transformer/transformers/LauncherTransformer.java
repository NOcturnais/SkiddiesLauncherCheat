package fr.noctu.sld.utils.transformer.transformers;

import fr.noctu.sld.Main;
import fr.noctu.sld.utils.transformer.LTransformer;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.InsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

public class LauncherTransformer implements LTransformer, Opcodes {

    @Override
    public byte[] visitClass(String className, ClassNode classNode) {
        if(classNode.name.contains("/Launcher")){
            for(MethodNode methodNode : classNode.methods){
                if(methodNode.name.equals("update")){
                    if(Main.disableAutoUpdate){
                        InsnList list = new InsnList();
                        list.add(new InsnNode(RETURN));
                        methodNode.instructions = list;
                    }
                }
            }
        }
        ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(classWriter);
        return classWriter.toByteArray();
    }
}
