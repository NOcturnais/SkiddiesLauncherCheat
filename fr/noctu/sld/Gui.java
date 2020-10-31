package fr.noctu.sld;

import fr.noctu.sld.utils.LauncherClassLoader;
import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Gui {
    public CheckBox disableAutoUpdateCB;
    public TextField pathTextBox;
    public Label statusText;

    public void runGame(ActionEvent actionEvent) {
        File jarPath = new File(pathTextBox.getText());
        if(jarPath.isFile() && jarPath.exists()){
            if(jarPath.getName().endsWith(".jar")){
                setStatusText("File validated");
                try {
                    Main.disableAutoUpdate = disableAutoUpdateCB.isSelected();
                    loadJarInClassLoader(jarPath);
                } catch (IOException | ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }else
                setStatusText("File is not a jar");
        }else
            setStatusText("Invalid path");
    }

    private void setStatusText(String message){
        statusText.setText("Status: " + message);
        Main.logger.log(message);
    }

    private void loadJarInClassLoader(File jar) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LauncherClassLoader classLoader = new LauncherClassLoader(((URLClassLoader)ClassLoader.getSystemClassLoader()).getURLs());
        classLoader.addURL(jar.toURI().toURL());
        List<String> classNames = new ArrayList<>();
        ZipInputStream zip = new ZipInputStream(new FileInputStream(jar));
        for (ZipEntry entry = zip.getNextEntry(); entry != null; entry = zip.getNextEntry()) {
            if (!entry.isDirectory() && entry.getName().endsWith(".class")) {
                String className = entry.getName().replace('/', '.');
                classNames.add(className.substring(0, className.length() - ".class".length()));
            }
        }
        for(String className : classNames){
            classLoader.loadClass(className);
            if(className.contains("LauncherFrame"))
                classLoader.invokeClass(className, new String[]{});
        }
    }
}
