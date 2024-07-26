package com.coppel.dto.purchaseOrder;

import lombok.Data;

import java.util.ArrayList;

@Data
public class PurchaseOrderDTO {


    public boolean success;
    public Header header;
    public ArrayList<DatumPurchaseOrderDTO> data;
    public Object messageKey;
    public Object message;
    public ArrayList<Object> errors;
    public ArrayList<Object> exceptions;
    public Messages messages;
    public Object rootCause;
    public String cloudComponent;
    public String cloudComponentHostName;
    public Object requestUri;
    public String statusCode;
}