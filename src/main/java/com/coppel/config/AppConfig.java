package com.coppel.config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.info.BuildProperties;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    private String urlMerchandising;

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

    private String apiProxy;
    private Integer apiPort;

    private String maxAttempts;
    private String retryTimeInterval;
    private String readTimeout;
    private String connectTimeout;   
}
