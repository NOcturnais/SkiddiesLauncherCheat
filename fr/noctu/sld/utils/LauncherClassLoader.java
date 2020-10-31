package fr.noctu.sld.utils;

import fr.noctu.sld.Main;
import fr.noctu.sld.utils.transformer.LTransformer;
import fr.noctu.sld.utils.transformer.transformers.LauncherTransformer;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class LauncherClassLoader extends URLClassLoader {
    private HashMap<String,Class<?>> cachedClasses = new HashMap<>();
    private ArrayList<String> classExceptions = new ArrayList<>();
    private ArrayList<LTransformer> transformers = new ArrayList<>();

    public LauncherClassLoader(URL[] urls) {
        super(urls);
        classExceptions.add("java.");
        classExceptions.add("javax.");
        transformers.add(new LauncherTransformer());
    }

    @Override
    public final Class<?> loadClass(String name) throws ClassNotFoundException {
        for (String className : classExceptions) {
            if (name.startsWith(className))
                return super.loadClass(name);
        }

        if (cachedClasses.containsKey(name))
            return cachedClasses.get(name);

        try {
            Main.logger.log("Transforming class: " + name);
            byte[] classbytes = streamToByteArray(Objects.requireNonNull(this.getResourceAsStream(name.replaceAll("\\.","/")+".class")));

            for(LTransformer transformers : transformers){
                ClassNode classNode = new ClassNode();
                ClassReader classReader = new ClassReader(classbytes);
                classReader.accept(classNode, ClassReader.EXPAND_FRAMES);
                classbytes = transformers.visitClass(name, classNode);
            }

            Class<?> transformedClass = defineClass(name, classbytes,0, classbytes.length, getClass().getProtectionDomain());
            cachedClasses.put(name,transformedClass);
            return transformedClass;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.loadClass(name);
    }

    public void invokeClass(String name, String[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class<?> c = loadClass(name);
        Method m = c.getMethod("main", args.getClass());
        m.setAccessible(true);
        int mods = m.getModifiers();
        if (m.getReturnType() != void.class || !Modifier.isStatic(mods) || !Modifier.isPublic(mods))
            throw new NoSuchMethodException("main");

        m.invoke(null, new Object[] { args });
    }

    @Override
    public void addURL(URL url) {
        super.addURL(url);
    }

    private static byte[] streamToByteArray(InputStream stream) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();

        int line = 0;
        while ((line = stream.read(buffer)) != -1) {
            os.write(buffer, 0, line);
        }
        stream.close();
        os.flush();
        os.close();
        return os.toByteArray();
    }
}
