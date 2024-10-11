package com.coppel.pubsub;

import com.coppel.config.AppConfig;
import com.coppel.config.GcpConfig;

import com.coppel.dto.LogCustom;
import com.coppel.dto.MerchandisingInfoDTO;
import com.coppel.dto.jsonin.Detail;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.jsonin.Lpn;
import com.coppel.dto.jsonout.*;
import com.coppel.dto.originalOrder.ItemDTO;
import com.coppel.dto.originalOrder.OriginalOrderDTO;
import com.coppel.entities.CatBodegas;
import com.coppel.entities.OriginalOrder;
import com.coppel.execeptions.AppNotFoundHandler;
import com.coppel.execeptions.ErrorGeneralException;
import com.coppel.mappers.JsonConverter;
import com.coppel.services.ASNTexcocoService;
import com.coppel.services.clients.OriginalOrderClient;
import com.coppel.services.impl.CatBodegasServiceImpl;
import com.coppel.services.impl.OriginalOrderServiceImpl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.stream.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;

import javax.annotation.PreDestroy;

import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class PubSubSuscriber {

    private final Logger logger = LoggerFactory.getLogger(PubSubSuscriber.class);
    private GcpConfig gcpConfig;
    private AppConfig appConfig;
    private final ScheduledExecutorService executorService;
    private Subscriber subscriber;
    private PublisherMessaje publisherMessaje;
    private final ASNTexcocoService asnTexcocoService;
    private final OriginalOrderServiceImpl originalOrderService;
    private final OriginalOrderClient originalOrderClient;
    private final CatBodegasServiceImpl catBodegasService;
    private static final String ITEM_NOT_FOUND = "No existe sku: %s en Manhattan";


    public PubSubSuscriber(GcpConfig gcpConfig, AppConfig appConfig, PublisherMessaje publisherMessaje, ASNTexcocoService asnTexcocoService, OriginalOrderServiceImpl originalOrderService, OriginalOrderClient originalOrderClient, CatBodegasServiceImpl catBodegasService) {
        this.gcpConfig = gcpConfig;
        this.appConfig = appConfig;
        this.publisherMessaje = publisherMessaje;
        this.originalOrderService = originalOrderService;
        this.originalOrderClient = originalOrderClient;
        this.catBodegasService = catBodegasService;
        this.executorService = Executors.newScheduledThreadPool(5);
        this.asnTexcocoService = asnTexcocoService;
    }


    @EventListener(ContextRefreshedEvent.class)
    public void start() {
        executorService.scheduleAtFixedRate(this::subscribeAsync, 0, 1, TimeUnit.MINUTES);
    }

    public void subscribeAsync() {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(appConfig.getProjectIdOrigen(), appConfig.getSubIdOrigen());

        MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
            try {
                String payload = message.getData().toStringUtf8();
                logger.info("Message received with id : " + message.getMessageId());
                JsonIn[] jsonInArray = new JsonConverter().fromJson(payload, JsonIn[].class);
                for (JsonIn jsonIn : jsonInArray) {
                    jsonIn.setPurchaseOrderLineDTOList(
                            asnTexcocoService.getPurchaseOrderFromManhattan(String.valueOf(jsonIn.getPurchaseOrderId())));
                    
                    
                    if(jsonIn.getAsnReference().length() == 36)
                    {
                        if (jsonIn.getSourceBusinessUnitId().equals(28)){
                            asnTexcocoService.processASNCanonicoRopa(jsonIn);
                            if(isOriginalOrder(jsonIn)){
                                //publicar en pubsub original order ropa
                                log.info("Intenta publicar en pubsub original order ropa");
                                publishOriginalOrderRopa(jsonIn);
                            }
                        }else if (jsonIn.getSourceBusinessUnitId().equals(30024)){
                            asnTexcocoService.processASNCanonicoMuebles(jsonIn);
                            if(isOriginalOrder(jsonIn)){
                                //publicar en pubsub original order muebles
                                publishOriginalOrderMuebles(jsonIn);
                            }
                        }
                    }
                    else{
                        processElement(jsonIn);
                    }
                }
                consumer.ack();
            } catch (Exception e) {
                logger.error(new LogCustom<>(e.getMessage(), Thread.currentThread().getStackTrace()[1].getMethodName(), message.getData().toStringUtf8(), null, null).toJson());
                consumer.ack();
            }
        };

        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).setCredentialsProvider(FixedCredentialsProvider.create(gcpConfig.googleCredentialsOrigen())).build();
            subscriber.startAsync().awaitRunning();
            String subName = subscriptionName.toString();
            logger.info("Listening for messages on {}", subName);
            subscriber.awaitTerminated();
        } catch (Exception e) {
            logger.error("Error running subscriber: {}", e.getMessage(), e);
            if (subscriber != null) {
                subscriber.stopAsync();
            }
        }
    }

    public void publishOriginalOrderRopa(JsonIn jsonIn){
        DateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'hh:mm:ss.SSS");
        Date fecha = new Date();
        try{

            OriginalOrderDTO originalOrderDTO  = new OriginalOrderDTO();
            originalOrderDTO.setOriginalOrderId("CDR"+ jsonIn.getPurchaseOrderId()+jsonIn.getReferenceId()+jsonIn.getSourceBusinessUnitId()+jsonIn.getDestinationBusinessUnitId()+"R");
            originalOrderDTO.setCreateDateTimestamp(dateFormat.format(fecha.getTime()));
            originalOrderDTO.setMinimumStatus("0000");
            originalOrderDTO.setMaximumStatus("0000");
            originalOrderDTO.setOrderType("CrossDocking");
            originalOrderDTO.setDestinationBusinessUnitId(getFuritureNumber(jsonIn.getDestinationBusinessUnitId()));
            originalOrderDTO.setSourceBusinessUnitId(getFuritureNumber(jsonIn.getSourceBusinessUnitId()));
            originalOrderDTO.setTypeCode(2);
            originalOrderDTO.setAsnReference(jsonIn.getAsnReference());
            List<ItemDTO> itemDTOList = new ArrayList<>();
            Lpn lpn = jsonIn.getLpns().get(0);
            Integer contador = 1;
            for (Detail det : lpn.getDetails()) {

                ItemDTO itemDTO = new ItemDTO();
                itemDTO.setOriginalOrderId(originalOrderDTO.getOriginalOrderId());
                itemDTO.setSku(det.getSku());
                itemDTO.setLineItemDetail(contador++);
                itemDTO.setStatus("0000");
                itemDTO.setRefurbishedUnitId("0");
                itemDTO.setUnitCount(det.getRetailUnitCount());
                itemDTO.setQuantitySupplied(0);
                itemDTO.setSingleLineOrder(false);
                itemDTO.setSingleUnitOrder(false);
                itemDTO.setSingleFragilOrder(false);
                itemDTO.setMultiLineOrder(false);
                itemDTO.setSourceBusinessUnitId(getFuritureNumber(jsonIn.getSourceBusinessUnitId()));
                itemDTO.setEmployeeId(0);
                itemDTO.setCenterNumber(0);
                itemDTO.setCurrentSaleUnitRetailPriceAmount(det.getCurrentSaleUnitRetailPriceAmount());
                itemDTO.setOrderFragmentId(null);
                itemDTO.setOrderFragmentDescription("");
                itemDTO.setReferenceID("");
                itemDTO.setBatchId("");
                itemDTO.setOrderCvesort(0);
                itemDTO.setOrderTdaFact("");
                itemDTO.setSequenceNumber(0);
                itemDTO.setOrderFact(null);
                itemDTOList.add(itemDTO);
                itemDTO = new ItemDTO();
            }
            originalOrderDTO.setUnitCount((int) lpn.getDetails().stream().count());
            originalOrderDTO.setItems(itemDTOList);
            OriginalOrder originalOrder = new OriginalOrder();
            originalOrder.setOriginalOrderId(originalOrderDTO.getOriginalOrderId());
            String payload = JsonConverter.convertObjectToJson(originalOrderDTO);
            originalOrder.setPayload(payload);
            originalOrderService.insertOriginalOrder(originalOrder);
            log.info("insert en tabla originalorderpreviousmanhattan ");
            originalOrderClient.postOriginalORder(originalOrderDTO);

        }catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
            Thread.currentThread().interrupt();

        }
    }

    private Integer getFuritureNumber(Integer clothingNumber){
        Integer furitureNumber = 0;
        CatBodegas catBodegas = catBodegasService.getFurnitureNumber(clothingNumber);
        furitureNumber = catBodegas.getNumBodegaMuebles();
        return  furitureNumber;
    }

    public void publishOriginalOrderMuebles(JsonIn jsonIn){
        DateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'hh:mm:ss.SSS");
        Date fecha = new Date();
        try{

            OriginalOrderDTO originalOrderDTO  = new OriginalOrderDTO();
            originalOrderDTO.setOriginalOrderId("CDM"+ dateFormat.format(fecha)+jsonIn.getDestinationBusinessUnitId()+jsonIn.getPurchaseOrderId()+"M");
            originalOrderDTO.setCreateDateTimestamp(dateFormat.format(fecha.getTime()));
            originalOrderDTO.setMinimumStatus("0000");
            originalOrderDTO.setMaximumStatus("0000");
            originalOrderDTO.setOrderType("CrossDocking");
            originalOrderDTO.setDestinationBusinessUnitId(jsonIn.getDestinationBusinessUnitId());
            originalOrderDTO.setSourceBusinessUnitId(jsonIn.getSourceBusinessUnitId());
            originalOrderDTO.setTypeCode(2);
            originalOrderDTO.setAsnReference(jsonIn.getAsnReference());
            List<ItemDTO> itemDTOList = new ArrayList<>();
            Lpn lpn = jsonIn.getLpns().get(0);
            Integer contador = 1;
            for (Detail det : lpn.getDetails()) {

                ItemDTO itemDTO = new ItemDTO();
                itemDTO.setOriginalOrderId(originalOrderDTO.getOriginalOrderId());
                itemDTO.setSku(det.getSku());
                itemDTO.setLineItemDetail(contador++);
                itemDTO.setStatus("0000");
                itemDTO.setRefurbishedUnitId(det.getRefurbishedUnitId().equals("0") ? "N" : det.getRefurbishedUnitId());
                itemDTO.setUnitCount(det.getRetailUnitCount());
                itemDTO.setQuantitySupplied(0);
                itemDTO.setSingleLineOrder(false);
                itemDTO.setSingleUnitOrder(false);
                itemDTO.setSingleFragilOrder(false);
                itemDTO.setMultiLineOrder(false);
                itemDTO.setSourceBusinessUnitId(jsonIn.getSourceBusinessUnitId());
                itemDTO.setEmployeeId(0);
                itemDTO.setCenterNumber(0);
                itemDTO.setCurrentSaleUnitRetailPriceAmount(det.getCurrentSaleUnitRetailPriceAmount());
                itemDTO.setOrderFragmentId(null);
                itemDTO.setOrderFragmentDescription("");
                itemDTO.setReferenceID("");
                itemDTO.setBatchId("");
                itemDTO.setOrderCvesort(0);
                itemDTO.setOrderTdaFact("");
                itemDTO.setSequenceNumber(0);
                itemDTO.setOrderFact(null);
                itemDTOList.add(itemDTO);
                itemDTO = new ItemDTO();
            }
            originalOrderDTO.setUnitCount((int) lpn.getDetails().stream().count());
            originalOrderDTO.setItems(itemDTOList);
            OriginalOrder originalOrder = new OriginalOrder();
            originalOrder.setOriginalOrderId(originalOrderDTO.getOriginalOrderId());
            String payload = JsonConverter.convertObjectToJson(originalOrderDTO);
            originalOrder.setPayload(payload);
            originalOrderService.insertOriginalOrder(originalOrder);
            log.info("insert en tabla originalorderpreviousmanhattan ");
            originalOrderClient.postOriginalORder(originalOrderDTO);

        }catch (Exception e) {
            logger.error("{}", e.getMessage(), e);
            Thread.currentThread().interrupt();

        }
    }

    public void processElement(JsonIn jsonIn) {
        try {
            boolean tienePrefijo;
            String asnOriginal = jsonIn.getAsnReference();
            String messageId = null;
            List<Lpn> lpns = jsonIn.getLpns();
            List<String> skus = new ArrayList<>();
            //Skus para buscar merchandasing
            for(Lpn lpn: lpns ){
                for (Detail det : lpn.getDetails()) {
                    skus.add(det.getSku());
                }
            }

            String[] prefijosASN = {"BIM","BIR","BIT","HAB","MRB","CT","MTA","AREA","SOB"};
            String prefijo = Arrays.stream(prefijosASN).filter(asnOriginal::startsWith)
                    .findFirst().orElse(null);
            tienePrefijo = StringUtils.isNoneBlank(prefijo);
            
            List<MerchandisingInfoDTO> skusMerchandisingInfo = asnTexcocoService.getMerchandisingInfo(skus);
            //Agrega prefijo y preciounitario
            List<Lpn> lpnsn = new ArrayList<>();
            List<Detail> detallesGeneral = new ArrayList<>();
            for(Lpn lpn: lpns){
                List<Detail> detalles = new ArrayList<>(); 
                for (Detail det : lpn.getDetails()) {
                    String merchandiseGroupId = "";
                    if(det.getSku().length() == 9 && !tienePrefijo)
                    {
                        prefijo = "BIR";
                    }
                    List<MerchandisingInfoDTO> filteredItems = skusMerchandisingInfo.stream()
                   .filter(item -> item.getSku().equals(det.getSku()))
                   .toList();
                    if(!filteredItems.isEmpty() && filteredItems.get(0).getMerchandiseGroupId() != null)
                    {
                        if (!tienePrefijo) {
                            merchandiseGroupId = filteredItems.get(0).getMerchandiseGroupId();
                            //M1,M2,M3 BIM;
                            if(merchandiseGroupId.equals("M1") || merchandiseGroupId.equals("M2") || merchandiseGroupId.equals("M3")){
                                prefijo = "BIM";
                            }
                            if(merchandiseGroupId.equals("M4") || merchandiseGroupId.equals("M5") || merchandiseGroupId.equals("M6") || merchandiseGroupId.equals("M7")){
                                prefijo = "BIT";
                            }
                        }
                        det.setCurrentSaleUnitRetailPriceAmount(filteredItems.get(0).getUnitCost().longValue());
                    }
                    else{
                        throw new AppNotFoundHandler(String.format(ITEM_NOT_FOUND, det.getSku()));
                    }
                    if (!tienePrefijo) {
                        det.setAsnReference(prefijo + det.getAsnReference());
                    }
                    if (prefijo.equals("MTA")){
                        asnOriginal = asnOriginal.replace("MTA","AREA");
                        det.setAsnReference(asnOriginal);
                    }
                    detalles.add(det);
                    lpn.setAsnReference(asnOriginal);
                    detallesGeneral.add(det);
                }
                lpn.setDetails(detalles);
                lpnsn.add(lpn);
            }
            jsonIn.setLpns(lpnsn);
            ArrayList<Datum> tmpData = new ArrayList<>();
            for(String prefijoASN: prefijosASN){
                Datum data;
                List<Detail> detalles = detallesGeneral.stream()
                   .filter(item -> item.getAsnReference().startsWith(prefijoASN))
                   .toList();
                if(!detalles.isEmpty()){
                    data = procesaJson(detalles,jsonIn,prefijoASN,tienePrefijo);
                    if (data != null && data.getDestinationFacilityId().equals("30024")) {
                        tmpData.add(data);
                    }
                }
            }
            String message = new JsonConverter().toJson(new JsonOut(tmpData));
            if (publisherMessaje != null) {
                if(!tmpData.isEmpty()){
                    asnTexcocoService.insertManhattanAsn(message, jsonIn.getAsnReference());
                    messageId = publisherMessaje.publishWithCustomAttributes(appConfig.getProjectIdDes(), appConfig.getTopicIdDes(), message);
                }
            } else {
                logger.warn("Publisher Message is not initialized, unable to publish message: {}", message);
            }
            logger.info(new LogCustom<>("200", Thread.currentThread().getStackTrace()[1].getMethodName(), messageId, null, message).toJson());
        } catch (InterruptedException | IOException | ExecutionException e) {
            logger.error("{}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (ErrorGeneralException e) {
            logger.error("{}", e.getMessage());
        } catch (AppNotFoundHandler e) {
            logger.error("{}",e.getMessage());
            asnTexcocoService.insertLog(jsonIn.getAsnReference(),e);
        }
    }

    private AsnLine getAsnLine(Detail det, JsonIn jsonIn, Lpn lpn) {
        AsnLine asnLine = new AsnLine();
        asnLine.setAsn(new Asn(jsonIn.getAsnReference()));
        //asnLine.setAsnLineId(Integer.toString(jsonIn.getLpns().get(0).getDetails().size()));//"1" o suma los item a nivel lpn
        asnLine.setBatchNumber(null);
        asnLine.setCanceled(false);
        asnLine.setExtended(new Extended("1"));
        String expiryDate = det.getExpiryDate();
        asnLine.setExpiryDate((expiryDate == null || det.getExpiryDate().equals("1900-01-01")) ? null : det.getExpiryDate());
        asnLine.setInventoryAttribute1(null);
        asnLine.setInventoryAttribute2((det.getRefurbishedUnitId().equals("0") ? "N":det.getRefurbishedUnitId()));
        asnLine.setInventoryTypeId("N");
        asnLine.setItemId(det.getSku());
        asnLine.setProductStatusId("InStock");
        asnLine.setQuantityUomId("UNIT");
        asnLine.setRetailPrice(det.getCurrentSaleUnitRetailPriceAmount().doubleValue());
        asnLine.setShippedQuantity(lpn.getTotalUnitCount().doubleValue());
        asnLine.setCountryOfOrigin("MEXICO");
        return asnLine;
    }

    @PreDestroy
    public void shutdown() {
        if (subscriber != null) {
            subscriber.stopAsync();
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("Interrupted!: {}", e.getMessage(), e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private String catAsnTypeCode(Integer asnTypeCode) {
        return switch (asnTypeCode) {
            case 1, 2 -> "P";
            case 3, 4 -> "S";
            default -> "W";
        };
    }

    private String getSourceBusinessUnit(int sourceBusinessUnitId) {
        if (sourceBusinessUnitId < 30000) {
            return String.format("T%03d", sourceBusinessUnitId).replace(' ', '0');
        } else if (sourceBusinessUnitId == 30024) {
            return "TEXCOCO";
        } else {
            return String.valueOf(sourceBusinessUnitId);
        }
    }

    private boolean isOriginalOrder(JsonIn jsonIn){
        if (!jsonIn.getSourceBusinessUnitId().equals(jsonIn.getDestinationBusinessUnitId())){
            return true;
        }else {
            return false;
        }
    }

    private Datum procesaJson(List<Detail> details, JsonIn jsonIn, String prefijo, boolean tienePrefijo){
        DateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'hh:mm:ss.SSS");
        Date fecha = new Date();

        Datum data = new Datum();
        double countOlpn = 0.0;
        String asnId =  tienePrefijo ?
                jsonIn.getAsnReference().replace("MTA","AREA") :
                prefijo + jsonIn.getAsnReference();
        asnId = asnId.replace("SOB","");
        data.setAsnId(asnId);
        if (Streams.of("MRB","CT","AREA").anyMatch(pref->pref.equals(prefijo))){
            data.setAsnLevelId("ITEM");
            data.setAsnOriginTypeId("S");
        } else {
            data.setAsnLevelId((Objects.equals(prefijo, "BIT")) ? "ITEM" : "LPN");
            data.setAsnOriginTypeId(Streams.of("HAB","SOB").anyMatch(pref->pref.equals(prefijo))  ? "S" : "W");
        }
        data.setAsnStatus("1000");
        data.setCanceled(false);
        data.setDestinationFacilityId(
                    jsonIn.getDestinationBusinessUnitId() < 30000 ?
                            String.format("T%04d", jsonIn.getDestinationBusinessUnitId()).replace(' ', '0') :
                            String.valueOf(jsonIn.getDestinationBusinessUnitId())
            );
        if(Streams.of("BIT","MRB","CT","AREA").anyMatch(pref->pref.equals(prefijo)))
        {
            //genera Asline
            data.setLpn(new ArrayList<>());
            List<AsnLine> asnLines = generaAsnLine(details); 
            data.setAsnLine(asnLines); 
        }
        else
        {
            //genera Olpn BIR y BIM
            data.setAsnLine(new ArrayList<>());
            List<LpnOut> lpns =  generaOlpn(details, jsonIn,prefijo);
            // validacion para checar que el DestinationBusinessUnitId pertenezca a TEXCOCO
            if(jsonIn.getLpns().stream().filter(item -> item.getDestinationBusinessUnitId() == 30024).count() > 0 ){
                  data.setDestinationFacilityId("30024");
            }
            countOlpn = lpns.size();
            data.setLpn(lpns);
        }
        if (Streams.of("BIT","BIR","BIM").anyMatch(pref->pref.equals(prefijo)))
        {
            data.setOriginFacilityId(jsonIn.getSourceBusinessUnitId().toString());
        } else if (Streams.of("CT").anyMatch(pref->pref.equals(prefijo)))
        {
            data.setOriginFacilityId(String.format("T%04d", jsonIn.getSourceBusinessUnitId()).replace(' ', '0'));
        }else
        {
            data.setOriginFacilityId("TEXCOCO");
        }
        data.setShippedDate(dateFormat.format(fecha.getTime()));
        data.setShippedLpns(countOlpn);
        data.setVendorId(null);
        return data;
    }
    private List<AsnLine> generaAsnLine(List<Detail> detalles) {
        List<AsnLine> asnLines = new ArrayList<>();
        int lineidsize = 0;
        //Crea olpn
        for (Detail det :detalles) {
            AsnLine asnLine = new AsnLine();
            asnLine.setAsn(new Asn(det.getAsnReference()));
            //asnLine.setAsnLineId(Integer.toString(jsonIn.getLpns().get(0).getDetails().size()));//"1" o suma los item a nivel lpn
            asnLine.setBatchNumber(null);
            asnLine.setCanceled(false);
            asnLine.setExtended(new Extended("1"));
            String expiryDate = det.getExpiryDate();
            asnLine.setExpiryDate((expiryDate == null || det.getExpiryDate().equals("1900-01-01")) ? null : det.getExpiryDate());
            asnLine.setInventoryAttribute1(null);
            asnLine.setInventoryAttribute2((det.getRefurbishedUnitId().equals("0")) ? "N":det.getRefurbishedUnitId());
            asnLine.setInventoryTypeId("N");
            asnLine.setItemId(det.getSku());
            asnLine.setProductStatusId("InStock");
            asnLine.setQuantityUomId("UNIT");
            asnLine.setRetailPrice(det.getCurrentSaleUnitRetailPriceAmount().doubleValue());
            asnLine.setShippedQuantity(det.getRetailUnitCount().doubleValue());
            asnLine.setCountryOfOrigin("MEXICO");
            asnLine.setAsnLineId(Integer.toString(++lineidsize));
            asnLines.add(asnLine);
        }
        return asnLines;
    }

    private List<LpnOut> generaOlpn(List<Detail> detalles, JsonIn jsonIn,String prefijo) {
        List<LpnOut> lpnOut = new ArrayList<>();
        List<Lpn> lpns = jsonIn.getLpns();
        //Filtra solo los destinos que contengan TEXCOCO = 30024
        List<Lpn> lpnsfiltrados = lpns.stream().filter(item -> item.getDestinationBusinessUnitId() == 30024 || item.getDestinationBusinessUnitId() == 0).toList();
        for(Lpn lpn: lpnsfiltrados ){
            //Crea olpn
            List<Detail> detallesn = detalles.stream()
                   .filter(item -> item.getLpnId().equals(lpn.getLpnId()))
                   .toList();



            if(!detallesn.isEmpty()){
                //crea olpn
                LpnOut lpnOutNuevo = new LpnOut();
                lpnOutNuevo.setLpnId(lpn.getLpnId());
                
                lpnOutNuevo.setLpnSizeTypeId((Objects.equals(prefijo,"BIM")) ? "Jaba Azul Chica": "Carton");
                List<LpnDetail> lpnDetails =  new ArrayList<>();
                int lineidsize = 0;
                for (Detail det :detallesn) {
                    LpnDetail lpnDetail = new LpnDetail();
                    lpnDetail.setLpnDetailId(Integer.toString(++lineidsize));
                    lpnDetail.setItemId(det.getSku());
                    lpnDetail.setExtended(new Extended(det.getCurrentSaleUnitRetailPriceAmount().toString()));
                    lpnDetail.setBatchNumber(null);
                    lpnDetail.setQuantityUomId("UNIT");                    
                    lpnDetail.setRetailPrice(det.getCurrentSaleUnitRetailPriceAmount().doubleValue());
                    lpnDetail.setShippedQuantity(det.getRetailUnitCount().doubleValue());
                    lpnDetail.setInventoryAttribute2( this.createInventoryAttribute2(prefijo, det) );
                    lpnDetail.setInventoryTypeId("N");
                    String expiryDate = det.getExpiryDate();
                    lpnDetail.setExpiryDate((expiryDate == null || expiryDate.equals("1900-01-01")) ? null : det.getExpiryDate());
                    lpnDetails.add(lpnDetail);
                }
                lpnOutNuevo.setPhysicalEntityCodeId("iLPN");
                lpnOutNuevo.setDetails(lpnDetails);
                lpnOut.add(lpnOutNuevo);
            }
        }
        return lpnOut;
    }

    private String createInventoryAttribute2(String prefijo, Detail det){
        if( Objects.equals(prefijo,"BIR")
                || Objects.equals(prefijo,"HAB")
                || Objects.equals(prefijo,"SOB"))
            return null;

        return det.getRefurbishedUnitId().trim().equals("0") ? "N" : det.getRefurbishedUnitId();
    }
}
