package mg.itu.app.tools;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.lang.annotation.Annotation;

public class Utils {

    public static List<String> getClassesContainingAnnotation(String packageName, Class<? extends Annotation> annotationClass) throws Exception {
        List<Class<?>> classes = getClasses(packageName);
        List<String> annotatedClasses = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                annotatedClasses.add(clazz.getName());
            }
        }

        return annotatedClasses;
    }

    public static List<Class<?>> getClasses(String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);

        File directory = new File(resource.toURI());

        List<Class<?>> classes = new ArrayList<>();

        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".class")) {

                String className = packageName + "." +
                        file.getName().replace(".class", "");

                classes.add(Class.forName(className));
            }
        }

        return classes;
    }
}