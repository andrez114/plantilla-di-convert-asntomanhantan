package com.coppel.dto.jsonout;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LpnDetail {
    @JsonProperty("LpnDeatilId")
    private String lpnDetailId;
    @JsonProperty("ItemId")
    private String itemId;
    @JsonProperty("Extended")
    private Extended extended;
    @JsonProperty("BatchNumber")
    private Object batchNumber;
    @JsonProperty("QuantityUomId")
    private String quantityUomId;
    @JsonProperty("RetailPrice")
    private Double retailPrice;
    @JsonProperty("ShippedQuantity")
    private Double shippedQuantity;
    @JsonProperty("InventoryAttribute2")
    private String inventoryAttribute2;
    @JsonProperty("InventoryTypeId")
    private String inventoryTypeId;
    @JsonProperty("ExpiryDate")
    private Object expiryDate;
}
