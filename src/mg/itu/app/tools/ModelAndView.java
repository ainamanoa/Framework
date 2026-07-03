package mg.itu.app.tools;
import java.util.HashMap;
import java.util.Map;

public class ModelAndView {
    private String view;
    private Map<String, Object> model = new HashMap<>();

    public ModelAndView(){}

    public ModelAndView(String view, Map<String, Object> model) {
        this.view = view;
        this.model = model;
    }

    public String getView() {
        return view;
    }

    public Map<String, Object> getModel() {
        return model;
    }

    public void setView(String view) {
        this.view = view;
    }

    public void setModel(Map<String, Object> model) {
        this.model = model;
    }

    public void addAttribute(String key, Object value) {
        model.put(key, value);
    }

    public Object getAttribute(String key) {
        return model.get(key);
    }
}
