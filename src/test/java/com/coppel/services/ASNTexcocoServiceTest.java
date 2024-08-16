package com.coppel.services;

import com.coppel.config.AppConfig;
import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.mappers.ASNCanonicoMapper;
import com.coppel.mappers.muebles.ASNCanonicoMueblesMapper;
import com.coppel.pubsub.PublisherMessaje;
import com.coppel.services.impl.ASNTexcocoServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ASNTexcocoServiceTest {
    @InjectMocks
    private ASNTexcocoService asnTexcocoService;
    @Mock
    private ASNCanonicoMapper asnCanonicoMapper;
    @Mock
    private PublisherMessaje publisherMessaje;
    @Mock
    private AppConfig appConfig;
    @Mock
    private ASNCanonicoMueblesMapper asnCanonicoMueblesMapper;
    @Mock
    private RestTemplate restTemplate;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        //asnTexcocoService = new ASNTexcocoServiceImpl(asnCanonicoMapper,asnCanonicoMueblesMapper, publisherMessaje, appConfig,restTemplate);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void processASNCanonicoRopa_happyPath() throws IOException, ExecutionException, InterruptedException {
        asnTexcocoService.processASNCanonicoRopa(objectMapper.readValue(new File("src/test/resources/jsons/jsonIn.json"), JsonIn.class));
        verify(asnCanonicoMapper, times(1)).toASNManhattan(any());
        verify(publisherMessaje, times(1)).publishWithCustomAttributes(any(), any(), any());
    }

    @Test
    void processASNCanonicoRopa_publisherMessageNull() throws IOException, ExecutionException, InterruptedException {
        //asnTexcocoService = new ASNTexcocoServiceImpl(asnCanonicoMapper, asnCanonicoMueblesMapper,null, appConfig,restTemplate);
        asnTexcocoService.processASNCanonicoRopa(objectMapper.readValue(new File("src/test/resources/jsons/jsonIn.json"), JsonIn.class));
        verify(publisherMessaje, times(0)).publishWithCustomAttributes(any(), any(), any());
    }

    @Test
    void processASNCanonicoRopa_AsnCanonicoNull() throws IOException, ExecutionException, InterruptedException {
        asnTexcocoService.processASNCanonicoRopa(null);
        verify(asnCanonicoMapper, times(0)).toASNManhattan(any());
        verify(publisherMessaje, times(0)).publishWithCustomAttributes(any(), any(), any());

    }

    @Test
    void publishASNToManhattan() throws IOException, ExecutionException, InterruptedException {
        asnTexcocoService.publishASNToManhattan(objectMapper.readValue(new File("src/test/resources/jsons/asnMessage.json"), ASNMessage.class));
        verify(publisherMessaje, times(1)).publishWithCustomAttributes(any(), any(), any());

    }

    @Test
    void publishASNToManhattan_publishMessageThrowsException() throws IOException, ExecutionException, InterruptedException {
        when(publisherMessaje.publishWithCustomAttributes(any(), any(), any())).thenThrow(new InterruptedException());
        asnTexcocoService.publishASNToManhattan(objectMapper.readValue(new File("src/test/resources/jsons/asnMessage.json"), ASNMessage.class));
        verify(publisherMessaje, times(1)).publishWithCustomAttributes(any(), any(), any());

    }
}