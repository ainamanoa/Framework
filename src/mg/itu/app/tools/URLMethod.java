package mg.itu.app.tools;
import java.util.Objects;

public class URLMethod {
    private String url;
    private String method;
    
    public URLMethod(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public URLMethod() {
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof URLMethod)) return false;
        

        URLMethod urlMethod = (URLMethod) obj;

        return url.equals(urlMethod.url) && method.equals(urlMethod.method);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, method);
    }
}
