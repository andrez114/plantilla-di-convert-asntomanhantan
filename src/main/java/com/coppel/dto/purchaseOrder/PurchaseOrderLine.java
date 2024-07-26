package com.coppel.dto.purchaseOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
public class PurchaseOrderLine {
    @JsonProperty("DeliveryStartDate")
    public Object deliveryStartDate;
    @JsonProperty("CreatedTimestamp")
    public Date createdTimestamp;
    @JsonProperty("CountryOfOrigin")
    public Object countryOfOrigin;
    @JsonProperty("UnShippedQuantity")
    public double unShippedQuantity;
    @JsonProperty("Process")
    public String process;
    @JsonProperty("QuantityUomId")
    public String quantityUomId;
    @JsonProperty("InventoryTypeId")
    public String inventoryTypeId;
    @JsonProperty("ItemId")
    public String itemId;
    @JsonProperty("OrderQuantity")
    public double orderQuantity;
    @JsonProperty("UpdatedBy")
    public String updatedBy;
    @JsonProperty("InventoryAttribute1")
    public Object inventoryAttribute1;
    @JsonProperty("InventoryAttribute2")
    public Object inventoryAttribute2;
    @JsonProperty("InventoryAttribute3")
    public Object inventoryAttribute3;
    @JsonProperty("PurgeDate")
    public Object purgeDate;
    @JsonProperty("InventoryAttribute4")
    public Object inventoryAttribute4;
    @JsonProperty("InventoryAttribute5")
    public Object inventoryAttribute5;
    @JsonProperty("PurchaseOrderLineId")
    public String purchaseOrderLineId;
    @JsonProperty("UpdatedTimestamp")
    public Date updatedTimestamp;
    @JsonProperty("CreatedBy")
    public String createdBy;
    @JsonProperty("BatchNumber")
    public Object batchNumber;
    @JsonProperty("BusinessUnitId")
    public Object businessUnitId;
    @JsonProperty("ShippedQuantity")
    public double shippedQuantity;
    @JsonProperty("DeliveryEndDate")
    public Object deliveryEndDate;
    @JsonProperty("EntityLabels")
    public Object entityLabels;
    @JsonProperty("ProductStatusId")
    public Object productStatusId;
    @JsonProperty("OrgId")
    public String orgId;
    @JsonProperty("FacilityId")
    public String facilityId;
    @JsonProperty("StandardPackQuantity")
    public Object standardPackQuantity;
    @JsonProperty("Closed")
    public boolean closed;
    @JsonProperty("PurchaseOrderId")
    public String purchaseOrderId;
    @JsonProperty("ContextId")
    public String contextId;
    @JsonProperty("PurchaseOrder")
    public PurchaseOrder purchaseOrder;
    @JsonProperty("PK")
    public String pK;
    @JsonProperty("Canceled")
    public boolean canceled;
    @JsonProperty("Unique_Identifier")
    public String unique_Identifier;
}

