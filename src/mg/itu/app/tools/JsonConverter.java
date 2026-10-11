package mg.itu.app.tools;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.lang.reflect.Type;

public class JsonConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String convertToJson(Object object) throws Exception {
        return mapper.writeValueAsString(object);
    }

    public static <T> T convertFromJson(String json, Class<T> clazz) throws Exception {
        return mapper.readValue(json, clazz);
    }

    public static <T> T convertFromJson(String json, TypeReference<T> typeReference) throws Exception {
        return mapper.readValue(json, typeReference);
    }

    public static JavaType getJavaType(Type type) {
        return mapper.getTypeFactory().constructType(type);
    }

    public static <T> T convertFromJson(String json, JavaType javaType) throws Exception {
        return mapper.readValue(json, javaType);
    }
}