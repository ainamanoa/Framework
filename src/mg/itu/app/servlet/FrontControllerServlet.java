package mg.itu.app.servlet;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.itu.app.tools.Utils;
import java.util.List;
import mg.itu.app.annotation.Controller;

public class FrontControllerServlet extends HttpServlet {
    private List<String> annotatedClasses;

    @Override
    public void init() throws ServletException {
        super.init();
        String packageName = this.getInitParameter("packageName");
        String[] packageNames = Utils.getPackageNames(packageName);

        try {
            annotatedClasses = Utils.getClassesContainingAnnotation(packageNames, Controller.class);
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

        if (annotatedClasses != null && !annotatedClasses.isEmpty()) {
            answer += "\nAnnotated classes:\n";
            for (String className : annotatedClasses) {
                answer += className + "\n";
            }
        } else {
            answer += "\nNo annotated classes found.";
        }

        out.println(answer);
    }
}