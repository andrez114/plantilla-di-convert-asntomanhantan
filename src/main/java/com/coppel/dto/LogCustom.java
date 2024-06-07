package com.coppel.dto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class LogCustom<T> {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private String transactionID;
    private String status;
    private String date;
    private String metodo;
    private String message;
    private String causa;
    private T data;

    public LogCustom(String status, String metodo, String message, String causa, T data) {
        this.date = DATE_FORMAT.format(LocalDateTime.now());
        this.metodo = metodo;
        this.message = message;
        this.causa = causa;
        this.data = data;
        this.status =status;
        this.transactionID = UUID.randomUUID().toString();
    }

    public String toJson() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode rootNode = mapper.createObjectNode();
            rootNode.put("TransactionID", transactionID);
            rootNode.put("Status", status);
            rootNode.put("Date", date);
            rootNode.put("Method", metodo);
            Optional.ofNullable(message).ifPresent(m -> rootNode.put("Message", m));
            Optional.ofNullable(causa).ifPresent(c -> rootNode.put("Cause", c));
            if (data!=null) rootNode.set("Data", mapper.valueToTree(data));

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}