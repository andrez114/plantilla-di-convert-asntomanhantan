package com.coppel.pubsub;

import com.coppel.config.GcpConfig;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class PublisherMessaje {
    GcpConfig gcpConfig;
    String projectId ;
    String topicId;

    private final Logger logger = LoggerFactory.getLogger(PublisherMessaje.class);

    public PublisherMessaje(GcpConfig gcpConfig){
        this.gcpConfig = gcpConfig;
        this.projectId = gcpConfig.getProjectIdOrigen();
        this.topicId = gcpConfig.gettopicIdOrigen();
    }



    @EventListener(ContextRefreshedEvent.class)
    public void start() throws IOException, ExecutionException, InterruptedException {
        publishMessage(topicId, projectId);
    }



    public String publishMessage( String topicId, String projectId)
            throws ExecutionException, InterruptedException, IOException {
            TopicName topicName = TopicName.of(projectId, topicId);
            Publisher publisher = null;
            String messageId = "Mensaje no enviado";
            try {
                publisher = Publisher.newBuilder(topicName)
                        .setCredentialsProvider(FixedCredentialsProvider.create(gcpConfig.googleCredentialsDestination()))
                        .build();

                String message = "mensaje de prueba";

                ByteString data = ByteString.copyFromUtf8(message);
                PubsubMessage pubsubMessage =
                        PubsubMessage.newBuilder()
                                .setData(data)
                                .build();
                ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);

                ApiFutures.addCallback(
                        messageIdFuture,
                        new ApiFutureCallback<String>() {

                            @Override
                            public void onFailure(Throwable throwable) {
                                if (throwable instanceof ApiException) {
                                    ApiException apiException = ((ApiException) throwable);
                                    // details on the API exception
                                    System.out.println(apiException.getStatusCode().getCode());
                                    System.out.println(apiException.isRetryable());
                                }
                                System.out.println("Error publishing message : " + message);
                            }

                            @Override
                            public void onSuccess(String messageId) {
                                // Once published, returns server-assigned message ids (unique within the topic)
                                System.out.println("Published message ID: " + messageId);
                            }
                        },
                        MoreExecutors.directExecutor());

                messageId = messageIdFuture.get();
                logger.info("Data : " + messageId);
                return messageId;
            } finally {
                if (publisher != null) {
                    publisher.shutdown();
                    publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }
}
