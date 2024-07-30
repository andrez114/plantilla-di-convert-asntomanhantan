package com.coppel.services.clients;

import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.originalOrder.OriginalOrderDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.net.URI;
@Slf4j
@Component
@RequiredArgsConstructor
public class OriginalOrderClient {
    @Value("${ws.wms.wms-sys-original-order.url}")
    private String sysOriginalOrderUrl;

    @Value("${ws.wms.wms-sys-original-order.paths.root}")
    private String sysOriginalOrderPath;

    private final WebConsumer webConsumer;

    ObjectMapper objectMapper = new ObjectMapper();

    @SuppressWarnings("rawtypes")
    public OriginalOrderDTO postOriginalORder(OriginalOrderDTO body){
        final URI sysAsnUri = URI.create(String.format("%s%s", sysOriginalOrderUrl, sysOriginalOrderPath));
        log.info("url servicio orignal order: " +sysAsnUri);
        Object response = webConsumer.post(sysAsnUri, MediaType.APPLICATION_JSON, body);
        ApiResponseDTO apiRes = objectMapper.convertValue(response, ApiResponseDTO.class);

        if(response == null) return null;
        return objectMapper.convertValue(apiRes.getData(), OriginalOrderDTO.class);
    }
}
