package mg.itu.app.tools;

import java.util.Enumeration;
import java.util.Map;
import java.util.HashMap;

import jakarta.servlet.http.HttpSession;

public class MaSession {
    private Map<String, Object> contain;

    public Map<String, Object> getContain() {
        return contain;
    }

    public void setContain(Map<String, Object> contain) {
        this.contain = contain;
    }

    public MaSession(Map<String, Object> contain) {
        this.setContain(contain);
    }

    public MaSession() {
        contain = new HashMap<>();
    }

    public void addAttribute(String name, Object value) {
        contain.put(name, value);
    }

    public Object getAttribute(String name) {
        return contain.get(name);
    }

    public void removeAttribute(String name) {
        contain.remove(name);
    }

    public void clear() {
        contain.clear();
    }

    public void copySessionToMap(HttpSession session) {
        this.clear();

        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            Object value = session.getAttribute(name);
            contain.put(name, value);
        }
    }

    public void updateHttpSession(HttpSession session) {
        Enumeration<String> attributeNames = session.getAttributeNames();

        while (attributeNames.hasMoreElements()) {
            String name = attributeNames.nextElement();
            session.removeAttribute(name);
        }

        for (Map.Entry<String, Object> entry : contain.entrySet()) {
            session.setAttribute(entry.getKey(), entry.getValue());
        }
    }
}