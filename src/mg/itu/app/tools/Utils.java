package mg.itu.app.tools;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import java.lang.reflect.Type;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import mg.itu.app.annotation.URLMapping;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.lang.reflect.Array;

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

    public static boolean isSimpleType(Class<?> type) {
        return type.equals(String.class)
            || type.equals(int.class)
            || type.equals(Integer.class)
            || type.equals(long.class)
            || type.equals(Long.class)
            || type.equals(double.class)
            || type.equals(Double.class)
            || type.equals(float.class)
            || type.equals(Float.class)
            || type.equals(boolean.class)
            || type.equals(Boolean.class)
            || type.equals(LocalDate.class)
            || type.equals(LocalDateTime.class);
    }

    public static String capitalizeFirstLetter(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    public static void set(Object obj, Field field, Object value) throws Exception {
        Class<?>[] params = {field.getType()};
        Object[] args = {value};

        Method method = obj.getClass().getDeclaredMethod("set"+capitalizeFirstLetter(field.getName()), params);
        method.invoke(obj, args);
    }

    public static Object bindingObject(Class<?> clazz, HttpServletRequest request) throws Exception {
        Object obj = clazz.getDeclaredConstructor().newInstance();

        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields ) {
            field.setAccessible(true);

            String name = field.getName();
            Object convValue;

            if (!isSimpleType(field.getType())) {
                convValue = bindingObject(field.getType(), request);
            } else {
                String value = request.getParameter(name);
    
                if (value == null) {
                    continue;
                }
    
                convValue = conversion(field.getType(), value);
            }

            set(obj, field, convValue);
        }

        return obj;
    }

    public static boolean isCollectionType(Class<?> type) {
        return Collection.class.isAssignableFrom(type);
    }

    public static boolean isArrayType(Class<?> type) {
        return type.isArray();
    }

    public static Object bindArray(Class<?> componentType, String parameterName,HttpServletRequest request) {

        String[] values = request.getParameterValues(parameterName);

        if (values == null) {
            values = new String[0];
        }

        // Création d'un tableau du type demandé
        Object array = Array.newInstance(componentType, values.length);

        for (int i = 0; i < values.length; i++) {
            Object value = conversion(componentType, values[i]);

            Array.set(array, i, value);
        }

        return array;
    }

    
    public static Object bindCollection( Parameter parameter, HttpServletRequest request) throws Exception {
        Class<?> collectionType = parameter.getType();
        Type genericType = parameter.getParameterizedType();

        if (!(genericType instanceof ParameterizedType)) {
            throw new IllegalArgumentException( "Le type de collection doit préciser son type d'élément : "+ parameter.getName()
            );
        }

        ParameterizedType parameterizedType = (ParameterizedType) genericType;
        Type elementType = parameterizedType.getActualTypeArguments()[0];

        if (!(elementType instanceof Class<?>)) {
            throw new IllegalArgumentException( "Type d'élément non pris en charge : " + elementType);
        }

        Class<?> elementClass = (Class<?>) elementType;

        Collection<Object> collection;

        if (collectionType.equals(List.class) || collectionType.equals(Collection.class)) {
            collection = new ArrayList<>();

        } else if (collectionType.equals(Set.class)) {
            collection = new LinkedHashSet<>();

        } else {
            collection = (Collection<Object>) collectionType.getDeclaredConstructor().newInstance();
        }

        if (isSimpleType(elementClass)) {

            String[] values =
                request.getParameterValues(parameter.getName());

            if (values != null) {
                for (String value : values) {
                    collection.add(conversion(elementClass, value));
                }
            }

            return collection;
        }

        int count = countObjectInstances(elementClass, request);

        for (int i = 0; i < count; i++) {
            Object obj = bindingObjectAtIndex(
                elementClass, request, i
            );

            collection.add(obj);
        }

        return collection;
    }

    public static int countObjectInstances( Class<?> clazz, HttpServletRequest request) {
        int max = 0;

        for (Field field : clazz.getDeclaredFields()) {
            if (isSimpleType(field.getType())) {
                String[] values =
                    request.getParameterValues(field.getName());

                if (values != null && values.length > max) {
                    max = values.length;
                }
            }
        }

        return max;
    }

    public static Object bindingObjectAtIndex(Class<?> clazz, HttpServletRequest request, int index) throws Exception {

        Object obj = clazz.getDeclaredConstructor().newInstance();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String name = field.getName();
            Class<?> fieldType = field.getType();

            if (isSimpleType(fieldType)) {
                String[] values = request.getParameterValues(name);

                if (values == null || index >= values.length) {
                    continue;
                }

                Object value = conversion(fieldType, values[index]);
                set(obj, field, value);

            } else {
                Object nested = bindingObjectAtIndex(
                    fieldType, request, index
                );

                set(obj, field, nested);
            }
        }

        return obj;
    }

    public static Object resolveParameter( Parameter parameter, HttpServletRequest request) throws Exception {

        Class<?> type = parameter.getType();

        if (isSimpleType(type)) {
            String value = request.getParameter(parameter.getName());
            return conversion(type, value);
        }

        if (type.isArray()) {
            return bindArray(type.getComponentType(), parameter.getName(), request);
        }

        if (Collection.class.isAssignableFrom(type)) {
            return bindCollection(parameter, request);
        }

        return bindingObject(type, request);
    }
}