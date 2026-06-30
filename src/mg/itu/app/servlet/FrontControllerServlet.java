package mg.itu.app.servlet;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.itu.app.tools.Utils;
import java.util.List;
import mg.itu.app.annotation.Controller;
import mg.itu.app.tools.URLInfo;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import mg.itu.app.annotation.URLMapping;
import mg.itu.app.tools.URLMethod;

public class FrontControllerServlet extends HttpServlet {
    private List<String> annotatedClasses;
    private Map<URLMethod, URLInfo> urlMappings = new HashMap<>();

    @Override
    public void init() throws ServletException {
        super.init();
        String packageName = this.getInitParameter("packageName");
        String[] packageNames = Utils.getPackageNames(packageName);

        try {
            annotatedClasses = Utils.getClassesContainingAnnotation(packageNames, Controller.class);
            Utils.getURLMappings(packageNames, Controller.class, URLMapping.class, urlMappings);
        } catch (Exception e) {
            throw new ServletException("Error initializing annotated classes", e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");

        PrintWriter out = response.getWriter();

        String path = request.getRequestURI();
        String context = request.getContextPath();
        String answer = path.substring(context.length());

        out.println("SPRINT 0: " + answer);

        String sprint1="";

        if (annotatedClasses != null && !annotatedClasses.isEmpty()) {
            sprint1 += "\nSPRINT1: ANNOTATED CLASSES:\n";
            for (String className : annotatedClasses) {
                sprint1 += className + "\n";
            }
        } else {
            sprint1 += "\nNo annotated classes found.";
        }

        boolean isURLMappingsPopulated = false;

        URLMethod targetURLMethod = new URLMethod(answer, request.getMethod());
        URLInfo urlInfo = urlMappings.get(targetURLMethod);

        if (urlInfo != null) {
            isURLMappingsPopulated = true;
            sprint1 += "\nSPRINT2:\n";  
            sprint1 += "\n Mapped to class: " + urlInfo.getClazz().getName() + ", method: " + urlInfo.getMethod().getName() + ", URL: " + targetURLMethod.getUrl() + ", HTTP Method: " + targetURLMethod.getMethod();

            try {
                Object controllerInstance = urlInfo.getClazz().getDeclaredConstructor().newInstance();
                Object result = urlInfo.getMethod().invoke(controllerInstance);

                if (result != null) {
                    sprint1 += "\nResult: " + result.toString();
                }
            } catch (Exception e) {
                e.printStackTrace(out);
            }
        }

        if (!isURLMappingsPopulated) {
            sprint1 += "\nSPRINT2:\n";  
            for (Map.Entry<URLMethod, URLInfo> entry : urlMappings.entrySet()) {
                URLInfo urlInf = entry.getValue();
                sprint1 += "\n Mapped to class: " + urlInf.getClazz().getName() + ", method: " + urlInf.getMethod().getName() + ", URL: " + entry.getKey().getUrl() + ", HTTP Method: " + entry.getKey().getMethod();
            }
        }

        out.println(sprint1);
    }
}