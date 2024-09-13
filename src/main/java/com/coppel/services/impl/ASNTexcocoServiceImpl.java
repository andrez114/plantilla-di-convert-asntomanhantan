package com.coppel.services.impl;

import com.coppel.config.AppConfig;
import com.coppel.dto.LogCustom;
import com.coppel.dto.MerchandisingInfoDTO;
import com.coppel.dto.asn.muebles.ASNManhattan;
import com.coppel.dto.asn.muebles.ASNMessageMuebles;
import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.purchaseOrder.PurchaseOrderDTO;
import com.coppel.dto.purchaseOrder.PurchaseOrderLineDTO;
import com.coppel.dto.token.AuthResponseDTO;
import com.coppel.entities.AsnToManhattan;
import com.coppel.entities.LogAsnManhattan;
import com.coppel.entities.ManhattanAsn;
import com.coppel.mappers.ASNCanonicoMapper;
import com.coppel.mappers.JsonConverter;
import com.coppel.mappers.muebles.ASNCanonicoMueblesMapper;
import com.coppel.pubsub.PublisherMessaje;
import com.coppel.repository.asn.LogAsnManhattanRepository;
import com.coppel.repository.asn.ManhattanAsnRepository;
import com.coppel.services.ASNTexcocoService;
import com.coppel.services.AsnToManhattanService;
import com.coppel.execeptions.ErrorGeneralException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.sql.Timestamp;
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
    private AsnToManhattanService asnToManhattanService;

    @Autowired 
    private LogAsnManhattanRepository logAsnManhattanRepository;
    @Autowired 
    private ManhattanAsnRepository manhattanAsnRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired 
    private AppConfig appConfig;



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
            String payloadmanhattan = JsonConverter.convertObjectToJson(asnMessageMuebles);
            asnToManhattanService.insertAsnId(asnToManhattan);
            //publishASNToManhattanMuebles(asnMessageMuebles);  // quitar esto y remplazarlo por el api rest
            asnToManhattanService.publishToManhattan(asnMessageMuebles);
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

    @Override
    public List<MerchandisingInfoDTO> getMerchandisingInfo(List<String> skus) throws ErrorGeneralException {

        try {
            String url = appConfig.getUrlMerchandising();

            HttpHeaders head = new HttpHeaders();
            head.setContentType(MediaType.APPLICATION_JSON);
            head.set("Authorization", "Bearer " + getAccessToken());

            String skuString = String.join(",", skus);
            String body = String.format(""" 
                    {
                        "Query": "ItemId in(%s)",
                        "Template": {
                            "ItemId": null,
                            "MerchandizingGroup":null,
                            "MerchandizingType":null,
                            "UnitCost": null
                        },
                        "Size": 10000
                    }
            """,skuString);

            HttpEntity<String> request = new HttpEntity<>(body, head);
            ResponseEntity<String> responseEntityStr = restTemplate.postForEntity(url, request, String.class);

            HttpStatusCode statusCode = responseEntityStr.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                String itemsArray = responseEntityStr.getBody();
                if (itemsArray != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode rootNode = objectMapper.readTree(itemsArray);
                    JsonNode dataNode = rootNode.path("data");
                    List<MerchandisingInfoDTO> merchandisingInfoList = new ArrayList<>();

                    if (dataNode.isArray()) {
                        for (JsonNode node : (ArrayNode) dataNode) {
                            MerchandisingInfoDTO merchandisingInfo = objectMapper.treeToValue(node, MerchandisingInfoDTO.class);
                            merchandisingInfoList.add(merchandisingInfo);
                        }
                    }
                    return merchandisingInfoList;
                } else {
                    throw new ErrorGeneralException("La respuesta del API no contiene datos válidos");
                }
            } else {
                throw new ErrorGeneralException("La solicitud al API no fue exitosa: " + statusCode);
            }
        } catch (Exception ex) {
            throw new ErrorGeneralException(String.format("Error general al enviar la solicitud: %s",ex.getMessage()));
        }
    }

    public void insertLog(String payload, Exception error){
        LogAsnManhattan lAsnManhattan = new LogAsnManhattan();
        lAsnManhattan.setDesPayload(payload);
        lAsnManhattan.setDesMotivo(error.getMessage());
        lAsnManhattan.setDesType("ASN");
        lAsnManhattan.setFecRegistro(new Timestamp(System.currentTimeMillis()));
        logAsnManhattanRepository.save(lAsnManhattan);
    }
    public void insertManhattanAsn(String payload,String asnRefence){
        ManhattanAsn manhattanAsn = new ManhattanAsn();
        manhattanAsn.setDesPayload(payload);
        manhattanAsn.setFecRegistro(new Timestamp(System.currentTimeMillis()));
        manhattanAsn.setAsnReference(asnRefence.replace("SOB",""));
        manhattanAsnRepository.save(manhattanAsn);
    }
}
