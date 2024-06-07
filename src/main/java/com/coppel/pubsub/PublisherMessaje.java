package com.coppel.pubsub;

import com.coppel.config.AppConfig;
import com.coppel.config.GcpConfig;
import com.google.api.gax.core.FixedCredentialsProvider;
import com.google.cloud.pubsub.v1.Publisher;
import org.springframework.stereotype.Component;
import com.google.api.core.ApiFuture;
import com.google.common.collect.ImmutableMap;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import com.google.pubsub.v1.TopicName;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Component
public class PublisherMessaje {

    GcpConfig gcpConfig;
    AppConfig appConfig;

    public PublisherMessaje(GcpConfig gcpConfig, AppConfig appConfig) {
        this.gcpConfig = gcpConfig;
        this.appConfig =appConfig;
    }

    public String publishWithCustomAttributes(String projectId, String topicId, String message)
            throws IOException, ExecutionException, InterruptedException {
        TopicName topicName = TopicName.of(projectId, topicId);
        Publisher publisher = null;
        String messageId = "Mensaje no enviado";
        try {
            publisher = Publisher.newBuilder(topicName)
                    .setCredentialsProvider(FixedCredentialsProvider.create(gcpConfig.googleCredentialsDestination()))
                    .build();
            ByteString data = ByteString.copyFromUtf8(message);
            PubsubMessage pubsubMessage =
                    PubsubMessage.newBuilder()
                            .setData(data)
                            .putAllAttributes(ImmutableMap.of("Organization", "TEXCOCO", "Location", "TEXCOCO", "User", "GCPUSER"))
                            .build();
            ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
            messageId = messageIdFuture.get();
            return messageId; 
        } finally {
            if (publisher != null) {
                publisher.shutdown();
                publisher.awaitTermination(1, TimeUnit.MINUTES);
            }
        }
    }
}
