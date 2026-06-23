package mg.itu.app.tools;

import java.lang.reflect.Method;

public class URLInfo {
    Class clazz;
    Method method;

    public URLInfo(Class clazz, Method method) {
        this.clazz = clazz;
        this.method = method;
    }

    public Class getClazz() {
        return clazz;
    }

    public Method getMethod() {
        return method;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public URLInfo(){}
}
