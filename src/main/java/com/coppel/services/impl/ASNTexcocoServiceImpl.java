package com.coppel.services.impl;

import com.coppel.config.AppConfig;
import com.coppel.dto.LogCustom;
import com.coppel.dto.asn.muebles.ASNManhattan;
import com.coppel.dto.asn.muebles.ASNMessageMuebles;
import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.purchaseOrder.PurchaseOrderDTO;
import com.coppel.dto.purchaseOrder.PurchaseOrderLineDTO;
import com.coppel.dto.token.AuthResponseDTO;
import com.coppel.entities.AsnToManhattan;
import com.coppel.mappers.ASNCanonicoMapper;
import com.coppel.mappers.JsonConverter;
import com.coppel.mappers.muebles.ASNCanonicoMueblesMapper;
import com.coppel.pubsub.PublisherMessaje;
import com.coppel.services.ASNTexcocoService;
import com.coppel.services.AsnToManhattanService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class ASNTexcocoServiceImpl implements ASNTexcocoService {
    private static final Number REQUIRED_ASN_TYPE_CODE = 1;
    private static final Long REQUIRED_TYPE_DOCUMENT_ASN_ID = 24L;
    private static final Long REQUIRED_TYPE_DOCUMENT_ASN_ID_MUEBLES = 25L;
    private static final Integer ASN_TEXCOCO = 28;
    private static final Integer ASN_TEXCOCO_MUEBLES = 30024;

    private ASNCanonicoMapper asnCanonicoMapper;
    private ASNCanonicoMueblesMapper asnCanonicoMueblesMapper;
    private PublisherMessaje publisherMessaje;
    private AppConfig appConfig;
    private RestTemplate restTemplate;
    private AsnToManhattanService asnToManhattanService;



    @Override
    public void processOrignalOrderRopa(JsonIn originalOrderROpa) {

    }

    @Override
    public void processASNCanonicoRopa(JsonIn asnCanonico) {
        if (isAsnRopaValidToPublish(asnCanonico)) {
            ASNMessage.ASNManhattan asnManhattan = asnCanonicoMapper.toASNManhattan(asnCanonico);
            AsnToManhattan asnToManhattan = new AsnToManhattan();
            asnToManhattan.setAsnId(asnManhattan.getAsnId());
            String payload = JsonConverter.convertObjectToJson(asnManhattan);
            asnToManhattan.setPayload(payload);
            asnToManhattanService.insertAsnId(asnToManhattan);
            publishASNToManhattan(ASNMessage.builder().data(Collections.singletonList(asnManhattan)).build());
        }
    }

    @Override
    public void processASNCanonicoMuebles(JsonIn asnCanonico) {
        if (isAsnMueblesValidToPublish(asnCanonico)) {
            ASNManhattan data = asnCanonicoMueblesMapper.toASNManhattan(asnCanonico);
            ASNMessageMuebles asnMessageMuebles = new ASNMessageMuebles();
            asnMessageMuebles.setData(Collections.singletonList(data));
            AsnToManhattan asnToManhattan = new AsnToManhattan();
            asnToManhattan.setAsnId(data.getAsnId());
            String payload = JsonConverter.convertObjectToJson(data);
            asnToManhattan.setPayload(payload);
            asnToManhattanService.insertAsnId(asnToManhattan);
            publishASNToManhattanMuebles(asnMessageMuebles);
        }
    }

    @Override
    public void publishASNToManhattan(ASNMessage asnManhattanMessage) {
        try {
            String message = new JsonConverter().toJson(asnManhattanMessage);
            String messageId;
            if (publisherMessaje != null) {
                messageId = publisherMessaje.publishWithCustomAttributes(appConfig.getProjectIdDes(), appConfig.getTopicIdDes(), message);
                log.info(new LogCustom<>("200", Thread.currentThread().getStackTrace()[1].getMethodName(), messageId, null, message).toJson());
            } else {
                log.warn("Publisher Message is not initialized, unable to publish message: {}", message);
            }
        } catch (InterruptedException | IOException | ExecutionException e) {
            log.error("{}", e.getMessage(), e);
            Thread.currentThread().interrupt();

        }
    }


    @Override
    public void publishASNToManhattanMuebles(ASNMessageMuebles asnManhattanMessage) {
        try {
            String message = new JsonConverter().toJson(asnManhattanMessage);
            String messageId = null;
            if (publisherMessaje != null) {
                messageId = publisherMessaje.publishWithCustomAttributes(appConfig.getProjectIdDes(), appConfig.getTopicIdDes(), message);
            } else {
                log.warn("Publisher Message is not initialized, unable to publish message: {}", message);
            }
            log.info(new LogCustom<>("200", Thread.currentThread().getStackTrace()[1].getMethodName(), messageId, null, message).toJson());
        } catch (InterruptedException | IOException | ExecutionException e) {
            log.error("{}", e.getMessage(), e);
            Thread.currentThread().interrupt();

        }
    }


    public boolean isAsnRopaValidToPublish(JsonIn asnCanonicoRequest) {
        if (Objects.isNull(asnCanonicoRequest)) {
            return false;
        }

        boolean isAsnTypeCodeValid = Objects.nonNull(asnCanonicoRequest.getAsnTypeCode())
                && asnCanonicoRequest.getAsnTypeCode().equals(REQUIRED_ASN_TYPE_CODE);

        boolean isTypeDocumentAsnIdValid = asnCanonicoRequest.getLpns().stream()
                .flatMap(lpn -> lpn.getDetails().stream())
                .allMatch(detail -> detail.getTypeDocumentAsnId().equals(REQUIRED_TYPE_DOCUMENT_ASN_ID));



        return isTypeDocumentAsnIdValid
                && isAsnTypeCodeValid;
    }

    public boolean isAsnMueblesValidToPublish(JsonIn asnCanonicoRequest) {
        if (Objects.isNull(asnCanonicoRequest)) {
            return false;
        }

        boolean isTypeDocumentAsnIdValid = asnCanonicoRequest.getLpns().stream()
                .flatMap(lpn -> lpn.getDetails().stream())
                .allMatch(detail -> detail.getTypeDocumentAsnId().equals(REQUIRED_TYPE_DOCUMENT_ASN_ID_MUEBLES));


        return isTypeDocumentAsnIdValid;
    }


    @Override
    public List<PurchaseOrderLineDTO> getPurchaseOrderFromManhattan(String purchaseOrder) {
        List<PurchaseOrderLineDTO> purchaseOrderLineList;
        String token = getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        headers.set("SelectedLocation", "TEXCOCO");
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String url = appConfig.getUrlServicePurchaseOrderManhattan() + purchaseOrder;

        PurchaseOrderDTO authResponseDTO = restTemplate.exchange(url, HttpMethod.GET, requestEntity, PurchaseOrderDTO.class).getBody();

        if (Objects.isNull(authResponseDTO)
                || authResponseDTO.data.isEmpty()
                || Objects.isNull(authResponseDTO.data.get(0).getPurchaseOrderLine().get(0).getPurchaseOrderLineId())) {
            log.warn("authResponseDTO for PurchaseOrderId {} is null", purchaseOrder);
            return new ArrayList<>();
        }

        purchaseOrderLineList = authResponseDTO.getData().stream()
                .flatMap(purchase -> purchase.getPurchaseOrderLine().stream())
                .map(pur -> {
                    PurchaseOrderLineDTO purchaseOrderLine = new PurchaseOrderLineDTO();
                    purchaseOrderLine.setItemId(pur.getItemId());
                    purchaseOrderLine.setPurchaseOrderLineId(pur.getPurchaseOrderLineId());
                    return purchaseOrderLine;
                })
                .collect(Collectors.toList());


        return purchaseOrderLineList;
    }



    private String getAccessToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Basic " + appConfig.getTokenAuthServiceManhattan());
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        String uri = appConfig.getUrlAuthServiceManhattan();
        AuthResponseDTO authResponseDTO = restTemplate.postForEntity(uri, requestEntity, AuthResponseDTO.class).getBody();

        if (Objects.isNull(authResponseDTO) || Objects.isNull(authResponseDTO.getAccessToken())) {
            System.out.println("AccessToken is null");
            return null;
        }

        return authResponseDTO.getAccessToken();
    }
}
