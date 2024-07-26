package com.coppel.dto.purchaseOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PurchaseOrder {

    @JsonProperty("PurchaseOrderId")
    public String purchaseOrderId;
}
