package mg.itu.app.tools;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonConverter {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static String convertToJson(Object object) throws Exception {
        return mapper.writeValueAsString(object);
    }
}