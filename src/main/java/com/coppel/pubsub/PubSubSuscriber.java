package com.coppel.pubsub;

import com.coppel.config.AppConfig;
import com.coppel.config.GcpConfig;

import com.coppel.dto.LogCustom;
import com.coppel.dto.jsonin.Detail;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.jsonin.Lpn;
import com.coppel.dto.jsonout.*;
import com.coppel.dto.originalOrder.ItemDTO;
import com.coppel.dto.originalOrder.OriginalOrderDTO;
import com.coppel.entities.CatBodegas;
import com.coppel.entities.OriginalOrder;
import com.coppel.mappers.JsonConverter;
import com.coppel.services.ASNTexcocoService;
import com.coppel.services.clients.OriginalOrderClient;
import com.coppel.services.impl.CatBodegasServiceImpl;
import com.coppel.services.impl.OriginalOrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
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

                    processElement(jsonIn);


                    if (jsonIn.getSourceBusinessUnitId().equals(28)){
                        asnTexcocoService.processASNCanonicoRopa(jsonIn);
                        if(isOriginalOrder(jsonIn)){
                            //publicar en pubsub original order ropa
                            log.info("Intenta publicar en pubsub original order ropa");
                            publishOriginalOrderRopa(jsonIn);
                        }
                    }else {
                        asnTexcocoService.processASNCanonicoMuebles(jsonIn);
                        if(isOriginalOrder(jsonIn)){
                            //publicar en pubsub original order muebles
                            publishOriginalOrderMuebles(jsonIn);
                        }
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
        try{

            OriginalOrderDTO originalOrderDTO  = new OriginalOrderDTO();
            originalOrderDTO.setOriginalOrderId("CDR"+ jsonIn.getPurchaseOrderId()+jsonIn.getReferenceId()+jsonIn.getSourceBusinessUnitId()+jsonIn.getDestinationBusinessUnitId()+"R");
            originalOrderDTO.setCreateDateTimestamp(dateFormat.format(Calendar.getInstance().getTime()));
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
                itemDTO.setRefurbishedUnitId(0);
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
        try{

            OriginalOrderDTO originalOrderDTO  = new OriginalOrderDTO();
            originalOrderDTO.setOriginalOrderId("CDM"+ dateFormat.format(Calendar.getInstance())+jsonIn.getDestinationBusinessUnitId()+jsonIn.getPurchaseOrderId()+"M");
            originalOrderDTO.setCreateDateTimestamp(dateFormat.format(Calendar.getInstance().getTime()));
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
                itemDTO.setRefurbishedUnitId(0);
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

            DateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'hh:mm:ss.SSS");
            String messageId = null;
            Datum data = new Datum();
            data.setAsnId(jsonIn.getAsnReference());
            data.setAsnLevelId("ITEM");
            data.setAsnOriginTypeId(catAsnTypeCode(jsonIn.getAsnTypeCode()));
            data.setAsnStatus("1000");
            data.setCanceled(false);
            data.setDestinationFacilityId(
                    jsonIn.getDestinationBusinessUnitId() < 30000 ?
                            String.format("T%03d", jsonIn.getDestinationBusinessUnitId()).replace(' ', '0') :
                            String.valueOf(jsonIn.getDestinationBusinessUnitId())
            );
            Lpn lpn = jsonIn.getLpns().get(0);
            List<AsnLine> asnLines = new ArrayList<>();
            int lineidsize = 0;
            for (Detail det : lpn.getDetails()) {
                AsnLine asnLine = getAsnLine(det, jsonIn, lpn);
                asnLine.setAsnLineId(Integer.toString(++lineidsize));
                asnLines.add(asnLine);
            }
            data.setAsnLine(asnLines);
            data.setLpn(new ArrayList<>());//jsonIn.getSourceBusinessUnitId()
            data.setOriginFacilityId("TEXCOCO");

            data.setShippedDate(dateFormat.format(Calendar.getInstance().getTime()));
            data.setShippedLpns(0.0);
            data.setVendorId(null);
            ArrayList<Datum> tmpData = new ArrayList<>();
            tmpData.add(data);
            String message = new JsonConverter().toJson(new JsonOut(tmpData));

            if (publisherMessaje != null) {
                if (data.getDestinationFacilityId().equals("30024")) {
                    messageId = publisherMessaje.publishWithCustomAttributes(appConfig.getProjectIdDes(), appConfig.getTopicIdDes(), message);
                }
            } else {
                logger.warn("Publisher Message is not initialized, unable to publish message: {}", message);
            }
            logger.info(new LogCustom<>("200", Thread.currentThread().getStackTrace()[1].getMethodName(), messageId, null, message).toJson());
        } catch (InterruptedException | IOException | ExecutionException e) {
            logger.error("{}", e.getMessage(), e);
            Thread.currentThread().interrupt();

        }
    }

    private AsnLine getAsnLine(Detail det, JsonIn jsonIn, Lpn lpn) {
        AsnLine asnLine = new AsnLine();
        asnLine.setAsn(new Asn(jsonIn.getAsnReference()));
        //asnLine.setAsnLineId(Integer.toString(jsonIn.getLpns().get(0).getDetails().size()));//"1" o suma los item a nivel lpn
        asnLine.setBatchNumber(null);
        asnLine.setCanceled(false);
        asnLine.setExtended(new Extended("1"));
        asnLine.setExpiryDate(null);
        asnLine.setInventoryAttribute1(null);
        asnLine.setInventoryAttribute2(null);
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

}
