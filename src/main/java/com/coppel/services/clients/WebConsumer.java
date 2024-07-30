package com.coppel.services.clients;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.net.URI;

@Component
@RequiredArgsConstructor
public class WebConsumer {
    private RestClient restClient = RestClient.create();


    public Object get(URI uri){
        ResponseEntity<Object> response = restClient.get()
                .uri(uri)
                .retrieve()
                .toEntity(Object.class);

        return response.getBody();
    }

    public Object post(URI uri, MediaType mediaType, Object body){

        ResponseEntity<Object> response = restClient.post()
                .uri(uri)
                .contentType(mediaType)
                .body(body)
                .retrieve()
                .toEntity(Object.class);

        return response.getBody();
    }
}