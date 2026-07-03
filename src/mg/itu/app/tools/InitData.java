package mg.itu.app.tools;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletException;
import mg.itu.app.annotation.URLMapping;
import jakarta.servlet.ServletContext;
import mg.itu.app.annotation.Controller;

public class InitData implements ServletContextListener {
    private List<String> annotatedClasses;
    private Map<URLMethod, URLInfo> urlMappings = new HashMap<>();

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        String packageName = context.getInitParameter("packageName");
        String[] packageNames = Utils.getPackageNames(packageName);


        try {
            annotatedClasses = Utils.getClassesContainingAnnotation(packageNames, Controller.class);
            Utils.getURLMappings(packageNames, Controller.class, URLMapping.class, urlMappings);
        } catch (Exception e) {
            throw new RuntimeException("Error initializing app", e);
        }

        context.setAttribute("annotatedClasses", annotatedClasses);
        context.setAttribute("urlMappings", urlMappings);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println("Tomcat s'arrête");
    }
}
