package com.coppel.dto;

import com.coppel.views.MetaView;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Meta
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Meta {

    @JsonView(value = { MetaView.External.class })
    private String transactionID;

    @JsonView(value = { MetaView.External.class })
    private String status;

    @JsonView(value = { MetaView.External.class })
    private int statusCode;

    @JsonView(value = { MetaView.External.class })
    private String timestamp;

    @JsonView(value = { MetaView.Internal.class })
    @JsonInclude(value = Include.NON_NULL)
    private String devMessage;

    @JsonView(value = { MetaView.External.class })
    @JsonInclude(value = Include.NON_NULL)
    private String message;

    public Meta(String status, int statusCode) {
        this.transactionID = UUID.randomUUID().toString();
        this.status = status;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now().toString();
    }

    public Meta(String status, int statusCode, String message) {
        this.transactionID = UUID.randomUUID().toString();
        this.status = status;
        this.statusCode = statusCode;
        this.timestamp = LocalDateTime.now().toString();
        this.message = message;
    }

    public Meta(String status, int statusCode, String message, String devMessage) {
        this.transactionID = UUID.randomUUID().toString();
        this.status = status;
        this.statusCode = statusCode;
        this.message = message;
        this.devMessage = devMessage;
        this.timestamp = LocalDateTime.now().toString();
    }
}
