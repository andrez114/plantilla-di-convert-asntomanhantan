package com.coppel.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Base64;

import org.springframework.context.annotation.Bean;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Configuration
public class GcpConfig {

    @Value("${app.origenAccess}")
    private String origenAccess;

    @Value("${app.desAccess}")
    private String desAccess;

    @Value("${app.projectIdOrigen}")
    private String projectIdOrigen;

    @Value("${app.subIdOrigen}")
    private String subIdOrigen;

    @Value("${app.topicIdOrigen}")
    private String topicIdOrigen;

    @Value("${app.subIdOrigenSecond}")
    private String subIdOrigenSecond;



    @Bean
    public GoogleCredentials googleCredentialsOrigen() throws IOException {
        return createGoogleCredentials(origenAccess);
    }

    @Bean
    public GoogleCredentials googleCredentialsDestination() throws IOException {
        return createGoogleCredentials(desAccess);
    }

    private GoogleCredentials createGoogleCredentials(String encodedKey) throws IOException {
        byte[] decodedKey = Base64.getDecoder().decode(encodedKey);
        try (InputStream credentialsStream = new ByteArrayInputStream(decodedKey)) {
            return ServiceAccountCredentials.fromStream(credentialsStream);
        }
    }

    public String getProjectIdOrigen() {
        return projectIdOrigen;
    }

    public String getSubIdOrigen() {
        return subIdOrigen;
    }

    public String getSubIdOrigenSecond() {
        return subIdOrigenSecond;
    }

    public String gettopicIdOrigen() {
        return topicIdOrigen;
    }
}