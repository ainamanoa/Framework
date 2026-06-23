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

public class FrontControllerServlet extends HttpServlet {
    private List<String> annotatedClasses;
    private Map<String, URLInfo> urlMappings = new HashMap<>();

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

        for (Map.Entry<String, URLInfo> entry : urlMappings.entrySet()) {

            if (entry.getKey().equals(answer)) {

                isURLMappingsPopulated = true;
                URLInfo urlInfo = entry.getValue();
                sprint1 += "\nSPRINT2:\n";  
                sprint1 += "\n Mapped to class: " + urlInfo.getClazz().getName() + ", method: " + urlInfo.getMethod().getName();
                break;
            }
        }

        if (!isURLMappingsPopulated) {
            sprint1 += "\nSPRINT2:\n";  
            for (Map.Entry<String, URLInfo> entry : urlMappings.entrySet()) {
                URLInfo urlInfo = entry.getValue();
                sprint1 += "\n Mapped to class: " + urlInfo.getClazz().getName() + ", method: " + urlInfo.getMethod().getName() + ", URL: " + entry.getKey();
            }
        }

        out.println(sprint1);
    }
}