package nova;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class NovaScriptFactory {

    public static NovaScript[] loadScriptsFromDreambot(String jarName) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

        LinkedList<NovaScript> loadedScripts = new LinkedList<>();

        String jarPath = String.format("./Scripts/%s.jar", jarName);
        JarFile jarFile = new JarFile(Paths.get(jarPath).toAbsolutePath().normalize().toString());
        Enumeration<JarEntry> enumOfJar = jarFile.entries();
        while (enumOfJar.hasMoreElements()) {
            ZipEntry entry = enumOfJar.nextElement();
            if (entry.isDirectory() || !entry.getName().startsWith("scripts/")) {
                continue;
            }

            NovaScript script = loadScript(entry.getName());
            loadedScripts.add(script);
        }

        return loadedScripts.toArray(new NovaScript[0]);
    }

    public static NovaScript loadScript(String scriptEntryName) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String[] pieces = scriptEntryName.split("/");
        String name = pieces[1];
        String classPath = String.format("scripts.%s.%s", name, name);
        Class<?> scriptClass = Class.forName(classPath);
        return (NovaScript) scriptClass.getDeclaredConstructor().newInstance();
    }
}
