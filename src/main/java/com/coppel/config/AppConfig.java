package com.coppel.config;

import java.util.Locale;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.client.RestTemplate;

/**
 * AppConfig
 */
@Component
@ConfigurationProperties(prefix = "app")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppConfig {


    private BuildProperties buildProperties;

    @Value("${server.servlet.context-path}")
    private String contextVersion;
    @Value("${app.urlAuthServiceManhattan}")
    private String urlAuthServiceManhattan;

    @Value("${app.tokenAuthServiceManhattan}")
    private String tokenAuthServiceManhattan;

    @Value("${app.urlServicePurchaseOrderManhattan}")
    private String urlServicePurchaseOrderManhattan;

    private String authUri;
    private boolean ignoreSession;
    private String allowedOrigins;
    private String allowedMethods;
    private String allowedHeaders;
    private String exposedHeaders;
    private String environment;

    private String origenAccess;
    private String projectIdOrigen;
    private String topicIdOrigen;
    private String subIdOrigen;

    private String desAccess;
    private String projectIdDes;
    private String topicIdDes;
    private String subIdDes;

    @Bean
    RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(new ObjectMapper());
        restTemplate.getMessageConverters().add(converter);
        return restTemplate;
    }


    public String compatibilityContextVersion() {
        int position = this.contextVersion.toLowerCase(Locale.ROOT).indexOf("/v", 0) + 2;
        String semanticContextVersion = this.contextVersion.substring(position);
        String majorVersionContext = semanticContextVersion.split("[.]")[0];
        String majorVersion = this.buildProperties.getVersion() == null
                ? majorVersionContext : this.buildProperties.getVersion().split("[.]")[0];

        if (majorVersionContext.equals(majorVersion)) {
            return this.buildProperties.getVersion();
        } else {
            return String.format("Major project version in pom.xml (%s), differs from major context version (%s)", majorVersion, semanticContextVersion);
        }
    }
}
