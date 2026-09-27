package mg.itu.app.servlet;

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import mg.itu.app.tools.Utils;
import java.util.List;
import mg.itu.app.annotation.Controller;
import mg.itu.app.annotation.RestAPI;
import mg.itu.app.tools.URLInfo;
import java.util.Map;
import java.util.HashMap;
import java.util.Map.Entry;
import mg.itu.app.annotation.URLMapping;
import mg.itu.app.tools.URLMethod;
import mg.itu.app.tools.ModelAndView;
import mg.itu.app.tools.Response;
import mg.itu.app.tools.JsonConverter;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public class FrontControllerServlet extends HttpServlet {
    private List<String> annotatedClasses;
    private Map<URLMethod, URLInfo> urlMappings;
    private String prefix;
    private String suffix;

    @Override
    public void init() throws ServletException {
        super.init();
        ServletContext context = getServletContext();

        prefix = this.getInitParameter("prefix");
        suffix = this.getInitParameter("suffix");

        try {
            annotatedClasses = (List<String>) context.getAttribute("annotatedClasses");
            urlMappings = (Map<URLMethod, URLInfo>) context.getAttribute("urlMappings");
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

        // out.println("SPRINT 0: " + answer);

        Map<String, String[]> params = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();

            out.println(key + "=" + String.join(",", values));
        }

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

                Object result = null;
                boolean hasParam = false;

                for (Class<?> paramType : urlInfo.getMethod().getParameterTypes()) {

                    if (paramType.equals(WebApplicationContext.class)) {

                        WebApplicationContext wac =
                            WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());

                        result = urlInfo.getMethod().invoke(controllerInstance, wac);

                        hasParam = true;
                        break;
                    }
                }

                if (!hasParam) {
                    result = urlInfo.getMethod().invoke(controllerInstance);
                }

                if (result != null) {
                    if (urlInfo.getMethod().isAnnotationPresent(RestAPI.class)) {

                        response.setContentType("application/json");
                        response.setCharacterEncoding("UTF-8");

                        if (result instanceof Response) {
                            response.getWriter().write(((Response) result).getBody());
                        } else {
                            try {
                                String json = JsonConverter.convertToJson(result);
                                response.getWriter().write(json);

                            } catch (Exception e) {
                                throw new ServletException("Erreur conversion JSON",e);
                            }
                        }
                        return;
                    }
                    if (result instanceof ModelAndView) {
                        ModelAndView modelAndView = (ModelAndView) result;
                        String viewName = modelAndView.getView();
                        Map<String, Object> model = modelAndView.getModel();

                        for ( Map.Entry<String, Object> entry : model.entrySet() ) {
                            String key = entry.getKey();
                            Object value = entry.getValue();

                            request.setAttribute(key, value);
                        }

                        request.getRequestDispatcher(prefix + viewName + suffix).forward(request, response);

                    } else {
                        sprint1 += "\nResult: " + result.toString();
                    }
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