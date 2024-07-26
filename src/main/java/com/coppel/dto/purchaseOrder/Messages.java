package com.coppel.dto.purchaseOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;

@Data
public class Messages {

    @JsonProperty("Message")
    public ArrayList<Object> message;
    @JsonProperty("Size")
    public int size;
}
