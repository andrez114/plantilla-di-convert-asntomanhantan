package com.coppel.dto.originalOrder;

import lombok.Builder;
import lombok.Data;

@Data

public class ItemDTO {

    public String originalOrderId;
    public String sku;
    public Integer lineItemDetail;
    public String status;
    public Integer refurbishedUnitId;
    public Long unitCount;
    public Integer quantitySupplied;
    public boolean singleLineOrder;
    public boolean singleUnitOrder;
    public boolean singleFragilOrder;
    public boolean multiLineOrder;
    public Integer sourceBusinessUnitId;
    public Integer employeeId;
    public Integer centerNumber;
    public double currentSaleUnitRetailPriceAmount;
    public Integer orderFragmentId;
    public String orderFragmentDescription;
    public String referenceID;
    public String batchId;
    public Integer orderCvesort;
    public String orderTdaFact;
    public Integer sequenceNumber;
    public String orderFact;
}
