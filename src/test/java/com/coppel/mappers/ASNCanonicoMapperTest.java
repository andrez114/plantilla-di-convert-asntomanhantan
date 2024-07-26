package com.coppel.mappers;

import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.JsonIn;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;

public class ASNCanonicoMapperTest {
    private ObjectMapper objectMapper;

    @Mock
    private LPNMapper lpnMapper;

    @InjectMocks
    ASNCanonicoMapper asnCanonicoMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toASNManhattan_expectedValues() throws IOException {
        lpnMapper.setVendorId("PR69906");
        ASNMessage.ASNManhattan response = asnCanonicoMapper.toASNManhattan(
                objectMapper.readValue(new File("src/test/resources/jsons/jsonIn.json"), JsonIn.class));
        assertNotNull(response);
        assertEquals("01AABB29-2582-11ED-A891-B58AD5D4B20F", response.getAsnId());
        assertEquals("LPN", response.getAsnLevelId());
        assertEquals("P", response.getAsnOriginTypeId());
        assertEquals("1000", response.getAsnStatus());
        assertTrue(response.isCanceled());
        assertEquals("TEXCOCO", response.getDestinationFacilityId());
        assertTrue(response.getLpn().isEmpty());
        assertEquals("TEXCOCO", response.getOriginFacilityId());
        assertEquals(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")), response.getShippedDate().substring(0, 16));
        assertEquals(1L, response.getShippedLpns());
        assertEquals("PR69906", response.getVendorId());
    }
}