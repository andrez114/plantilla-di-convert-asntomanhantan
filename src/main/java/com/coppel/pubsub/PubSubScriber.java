package com.coppel.pubsub;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.coppel.config.GcpConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.cloud.pubsub.v1.Subscriber;

import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PubSubScriber {


    private final Logger logger = LoggerFactory.getLogger(PubSubScriber.class);

    private GcpConfig gcpConfig;
    private final ScheduledExecutorService executorService;



    public PubSubScriber(GcpConfig gcpConfig){
        this.gcpConfig = gcpConfig;
        this.executorService = Executors.newScheduledThreadPool(5);
    }

    @EventListener(ContextRefreshedEvent.class)
    public void start() {
        executorService.scheduleAtFixedRate(this::subcriberAsync, 0, 1, TimeUnit.MINUTES);
    }


    public void subcriberAsync(){

        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of(gcpConfig.getProjectIdOrigen(), gcpConfig.getSubIdOrigen());


        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    logger.info("Message received with id : " + message.getMessageId());
                    logger.info("Data : " + message.getData().toStringUtf8());
                    consumer.ack();
                };

        Subscriber subscriber = null;
        try {
            subscriber = Subscriber.newBuilder(subscriptionName, receiver).setCredentialsProvider(FixedCredentialsProvider.create(gcpConfig.googleCredentialsOrigen())).build();
            // Start the subscriber.
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
}

