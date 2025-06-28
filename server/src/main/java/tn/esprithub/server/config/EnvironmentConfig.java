package tn.esprithub.server.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.stream.Stream;

/**
 * Environment post-processor that loads .env file automatically
 * This allows the application to work without the start script
 */
public class EnvironmentConfig implements EnvironmentPostProcessor {

    private static final String ENV_FILE = ".env";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        Resource resource = new FileSystemResource(ENV_FILE);
        if (resource.exists()) {
            loadEnvFile(environment, resource);
        } else {
            // Try to find .env in the project root
            Path projectRoot = Paths.get("").toAbsolutePath();
            Path envFile = projectRoot.resolve(ENV_FILE);
            if (Files.exists(envFile)) {
                loadEnvFile(environment, new FileSystemResource(envFile.toFile()));
            }
        }
    }

    private void loadEnvFile(ConfigurableEnvironment environment, Resource resource) {
        try {
            Properties properties = new Properties();
            
            // Read the .env file line by line using try-with-resources
            try (Stream<String> lines = Files.lines(resource.getFile().toPath())) {
                lines.filter(line -> !line.trim().isEmpty() && !line.trim().startsWith("#"))
                    .forEach(line -> {
                        String[] parts = line.split("=", 2);
                        if (parts.length == 2) {
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            // Remove quotes if present
                            if ((value.startsWith("\"") && value.endsWith("\"")) ||
                                (value.startsWith("'") && value.endsWith("'"))) {
                                value = value.substring(1, value.length() - 1);
                            }
                            properties.setProperty(key, value);
                        }
                    });
            }
            
            environment.getPropertySources().addLast(
                new PropertiesPropertySource("envFile", properties)
            );
            
        } catch (IOException e) {
            // Don't fail the application startup if .env file can't be loaded
            // This allows the app to work with system environment variables
        }
    }
}
