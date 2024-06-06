package com.coppel.pubsub;

import com.coppel.config.AppConfig;
import com.coppel.config.GcpConfig;

import com.coppel.dto.LogCustom;
import com.coppel.dto.jsonin.Detail;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.jsonin.Lpn;
import com.coppel.dto.jsonout.*;
import com.coppel.mappers.JsonConverter;
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

@Component
public class PubSubSuscriber {

    private final Logger logger = LoggerFactory.getLogger(PubSubSuscriber.class);
    private GcpConfig gcpConfig;
    private AppConfig appConfig;
    private final ScheduledExecutorService executorService;
    private Subscriber subscriber;
    private PublisherMessaje publisherMessaje;


    public PubSubSuscriber(GcpConfig gcpConfig, AppConfig appConfig, PublisherMessaje publisherMessaje) {
        this.gcpConfig = gcpConfig;
        this.appConfig = appConfig;
        this.publisherMessaje = publisherMessaje; // Aquí se inicializa con el valor pasado como argumento al constructor
        this.executorService = Executors.newScheduledThreadPool(5);
    }


    @EventListener(ContextRefreshedEvent.class)
    public void start() {
        executorService.scheduleAtFixedRate(this::subscribeAsync, 0, 1, TimeUnit.MINUTES);
    }

    public void subscribeAsync() {
        ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(appConfig.getProjectIdOrigen(), appConfig.getSubIdOrigen());
        MessageReceiver receiver = (PubsubMessage message, AckReplyConsumer consumer) -> {
            String payload = message.getData().toStringUtf8();
            logger.info("Received message");
            processElement(payload);
            consumer.ack();
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


    public void processElement(String payload) {
        try {
            JsonIn jsonIn = new JsonConverter().fromJson(payload, JsonIn.class);
            DateFormat dateFormat = new SimpleDateFormat("y-MM-dd'T'hh:mm:ss.SSS");
            String messageId = null;
            Datum data = new Datum();
            data.setAsnId(jsonIn.getAsnReference());
            data.setAsnLevelId("ITEM");
            data.setAsnOriginTypeId(jsonIn.getAsnTypeCode().toString());
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

            data.setShippedDate(dateFormat.format(Calendar.getInstance().getTime()));// Añadir fecha actual
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

    private String getSourceBusinessUnit(int sourceBusinessUnitId) {
        if (sourceBusinessUnitId < 30000) {
            return String.format("T%03d", sourceBusinessUnitId).replace(' ', '0');
        } else if (sourceBusinessUnitId == 30024) {
            return "TEXCOCO";
        } else {
            return String.valueOf(sourceBusinessUnitId);
        }
    }

}
