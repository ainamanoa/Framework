package mg.itu.app.tools;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import mg.itu.app.annotation.URLMapping;
import java.time.LocalDate;
import java.time.LocalDateTime;

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
                    URLMethod urlMethod = new URLMethod(url.url(), url.method());

                    if (mappings.containsKey(urlMethod)) {
                        throw new Exception("Duplicate URL mapping found for " + url.url() + " with method " + url.method());
                    }

                    mappings.put(urlMethod, urlInfo);
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

    public static Object conversion(Class<?> type, String value) {
        if (value == null) {
            return null;
        }

        Object resultat = null;
        
        if (type.equals(String.class)) { 
            resultat = value; 
        } else if (type.equals(int.class) || type.equals(Integer.class)) { 
            resultat = Integer.parseInt(value); 
        } else if (type.equals(long.class) || type.equals(Long.class)) { 
            resultat = Long.parseLong(value); 
        } else if (type.equals(double.class) || type.equals(Double.class)) { 
            resultat = Double.parseDouble(value); 
        } else if (type.equals(float.class) || type.equals(Float.class)) { 
            resultat = Float.parseFloat(value); 
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) { 
            resultat = Boolean.parseBoolean(value); 
        } else if (type.equals(LocalDate.class)) { 
            resultat = LocalDate.parse(value); 
        } else if (type.equals(LocalDateTime.class)) { 
            resultat = LocalDateTime.parse(value); 
        }

        return resultat;
    }
}