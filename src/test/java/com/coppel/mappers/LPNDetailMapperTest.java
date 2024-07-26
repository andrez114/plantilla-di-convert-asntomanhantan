package com.coppel.mappers;

import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.JsonIn;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class LPNDetailMapperTest {
    private ObjectMapper objectMapper;
    @InjectMocks
    private LPNDetailMapper lpnDetailMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toLpnDetailManhattan_expectedValues() throws IOException {
        lpnDetailMapper.setPurchaseOrderId("77777788");
        lpnDetailMapper.setVendorId("PR69906");
        List<ASNMessage.ASNManhattan.LPN.LPNDetail> response = lpnDetailMapper.mapAll(
                objectMapper.readValue(new File("src/test/resources/jsons/jsonIn.json"), JsonIn.class)
                        .getLpns().get(0).getDetails()
        );
        assertNotNull(response);
        ASNMessage.ASNManhattan.LPN.LPNDetail detail = response.get(0);
        assertEquals("1", detail.getAsnLineId());
        assertNull(detail.getBatchNumber());
        assertEquals("MEXICO", detail.getCountryOfOrigin());
        assertEquals("99", detail.getExtended().getComparisonPrice());
        assertNull(detail.getExpiryDate());
        assertNull(detail.getInventoryAttribute1());
        assertNull(detail.getInventoryAttribute2());
        assertNull(detail.getInventoryAttribute3());
        assertNull(detail.getInventoryAttribute4());
        assertNull(detail.getInventoryAttribute5());
        assertEquals("N", detail.getInventoryTypeId());
        assertEquals("741419010", detail.getItemId());
        assertEquals("1", detail.getLpnDetailId());
        assertEquals("1000", detail.getLpnDetailStatus());
        assertEquals("InStock", detail.getProductStatusId());
        assertEquals("77777788", detail.getPurchaseOrderId());
        assertEquals("UNIT", detail.getQuantityUomId());
        assertEquals("99", detail.getRetailPrice());
        assertEquals("50", detail.getShippedQuantity());
        assertEquals("PR69906", detail.getVendorId());
    }
}