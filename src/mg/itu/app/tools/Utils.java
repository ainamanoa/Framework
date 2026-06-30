package mg.itu.app.tools;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mg.itu.app.annotation.URLMapping;

public class Utils {

    public static List<String> getClassesContainingAnnotation(String[] packageNames, Class<? extends Annotation> annotationClass) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        for (String packageName : packageNames) {
            classes.addAll(getClasses(packageName));
        }
        
        List<String> annotatedClasses = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                annotatedClasses.add(clazz.getName());
            }
        }

        return annotatedClasses;
    }

    public static void getURLMappings(String[] packageNames, Class<? extends Annotation> annotationClass, Class<? extends Annotation> methodAnnotationClass, Map<URLMethod, URLInfo> mappings) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        
        for (String packageName : packageNames) {
            classes.addAll(getClasses(packageName));
        }
        
        List<Class<?>> annotatedClasses = new ArrayList<>();

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(annotationClass)) {
                annotatedClasses.add(clazz);
            }
        }

        for (Class<?> clazz : annotatedClasses) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.isAnnotationPresent(methodAnnotationClass)) {
                    URLInfo urlInfo = new URLInfo();
                    urlInfo.setClazz(clazz);
                    urlInfo.setMethod(method);

                    URLMapping url = method.getAnnotation(URLMapping.class);

                    mappings.put(new URLMethod(url.url(), url.method()), urlInfo);
                }
            }
        }
    }

    public static List<Class<?>> getClasses(String packageName) throws Exception {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        String path = packageName.replace('.', '/');
        URL resource = classLoader.getResource(path);

        List<Class<?>> classes = new ArrayList<>();
        
        if (resource==null){
            return classes;
        }

        File directory = new File(resource.toURI());


        for (File file : directory.listFiles()) {
            if (file.getName().endsWith(".class")) {

                String className = packageName + "." +
                        file.getName().replace(".class", "");

                classes.add(Class.forName(className));
            }
        }

        return classes;
    }

    public static String[] getPackageNames(String name) {
        return name.split(";");
    }
}