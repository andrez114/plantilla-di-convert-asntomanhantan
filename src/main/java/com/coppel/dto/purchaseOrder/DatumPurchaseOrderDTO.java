package com.coppel.dto.purchaseOrder;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;

@Data
public class DatumPurchaseOrderDTO {

    @JsonProperty("Messages")
    public Object messages;
    @JsonProperty("DeliveryStartDate")
    public Object deliveryStartDate;
    @JsonProperty("CreatedTimestamp")
    public Date createdTimestamp;
    @JsonProperty("OriginFacilityAliasId")
    public Object originFacilityAliasId;
    @JsonProperty("VendorId")
    public String vendorId;
    @JsonProperty("Process")
    public String process;
    @JsonProperty("DestinationFacilityAliasId")
    public Object destinationFacilityAliasId;
    @JsonProperty("PurchaseOrderLine")
    public ArrayList<PurchaseOrderLine> purchaseOrderLine;
    @JsonProperty("UpdatedBy")
    public String updatedBy;
    @JsonProperty("PurchaseOrderStatus")
    public String purchaseOrderStatus;
    @JsonProperty("PurgeDate")
    public Object purgeDate;
    @JsonProperty("OriginFacilityId")
    public String originFacilityId;
    @JsonProperty("DestinationFacilityId")
    public String destinationFacilityId;
    @JsonProperty("UpdatedTimestamp")
    public Date updatedTimestamp;
    @JsonProperty("CreatedBy")
    public String createdBy;
    @JsonProperty("BusinessUnitId")
    public Object businessUnitId;
    @JsonProperty("DeliveryEndDate")
    public Object deliveryEndDate;
    @JsonProperty("EntityLabels")
    public Object entityLabels;
    @JsonProperty("OrgId")
    public String orgId;
    @JsonProperty("FacilityId")
    public String facilityId;
    @JsonProperty("Closed")
    public boolean closed;
    @JsonProperty("PurchaseOrderId")
    public String purchaseOrderId;
    @JsonProperty("ContextId")
    public String contextId;
    @JsonProperty("PK")
    public String pK;
    @JsonProperty("Canceled")
    public boolean canceled;
    @JsonProperty("Unique_Identifier")
    public String unique_Identifier;
}
