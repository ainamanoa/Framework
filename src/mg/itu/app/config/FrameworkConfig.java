package mg.itu.app.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class FrameworkConfig {
    private static final Properties properties = new Properties();

    static {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("framework.properties")) {
            if (input == null) {
                throw new IllegalStateException("framework.properties introuvable");
            }
            properties.load(input);

        } catch (IOException e) {
            throw new ExceptionInInitializerError("Erreur lors du chargement de framework.properties: " + e.getMessage());
        }
    }

    public static String getUploadDirectory() {
        return properties.getProperty("upload.directory").trim();
    }
}