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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LPNMapperTest {
    private ObjectMapper objectMapper;
    @InjectMocks
    private LPNMapper lpnMapper;
    @Mock
    private LPNDetailMapper lpnDetailMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void toLPNManhattan_expectedValues() throws IOException {
        lpnMapper.setVendorId("PR69906");
        lpnMapper.setPurchaseOrderId("77777788");
        List<ASNMessage.ASNManhattan.LPN> response = lpnMapper.mapAll(
                objectMapper.readValue(new File("src/test/resources/jsons/jsonIn.json"), JsonIn.class)
                        .getLpns()
        );

        ASNMessage.ASNManhattan.LPN lpn = response.get(0);
        assertNotNull(response);
        assertEquals("01AABB29-2582-11ED-A891-B58AD5D4B20F", lpn.getAsnId());
        assertFalse(lpn.isCanceled());
        assertTrue(lpn.getLpnDetail().isEmpty());
        assertEquals("2828678903", lpn.getLpnId());
        assertNull(lpn.getLpnSizeTypeId());
        assertNull(lpn.getLpnStatus());
        assertNull(lpn.getLpnTypeId());
        assertEquals("iLPN", lpn.getPhysicalEntityCodeId());
        assertEquals("77777788", lpn.getPurchaseOrderId());
        assertEquals("PR69906", lpn.getVendorId());
    }
}