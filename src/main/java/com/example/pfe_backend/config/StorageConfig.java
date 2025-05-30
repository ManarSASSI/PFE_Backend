package com.example.pfe_backend.config;


import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class StorageConfig {
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Bean
    public Path uploadPath() {
        Path path = Paths.get(uploadDir);
        try {
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            return path;
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }
}
