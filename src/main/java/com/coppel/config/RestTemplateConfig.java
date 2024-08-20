package com.coppel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Autowired
    AppConfig config;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(Integer.parseInt(String.valueOf(config.getConnectTimeout()))))
                .setReadTimeout(Duration.ofSeconds(Long.parseLong(String.valueOf(config.getReadTimeout()))))
                .build();

        if (!config.getApiProxy().equals("")) {
            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(config.getApiProxy(), config.getApiPort()));
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
            requestFactory.setProxy(proxy);

            restTemplate.setRequestFactory(requestFactory);
        }

        return restTemplate;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        int maxAttempt = Integer.parseInt(config.getMaxAttempts());
        int retryTimeInterval = Integer.parseInt(config.getRetryTimeInterval());

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();
        retryPolicy.setMaxAttempts(maxAttempt);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(retryTimeInterval);

        RetryTemplate template = new RetryTemplate();
        template.setRetryPolicy(retryPolicy);
        template.setBackOffPolicy(backOffPolicy);

        return template;
    }
}
