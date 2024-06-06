
package com.coppel.dto.jsonout;

import com.fasterxml.jackson.annotation.JsonProperty;

public class AsnLine {

    @JsonProperty("Asn")
    private Asn asn;
    @JsonProperty("AsnLineId")
    private String asnLineId;
    @JsonProperty("BatchNumber")
    private Object batchNumber;
    @JsonProperty("Canceled")
    private Boolean canceled;
    @JsonProperty("CountryOfOrigin")
    private String countryOfOrigin;
    @JsonProperty("ExpiryDate")
    private Object expiryDate;
    @JsonProperty("Extended")
    private Extended extended;
    @JsonProperty("InventoryAttribute1")
    private Object inventoryAttribute1;
    @JsonProperty("InventoryAttribute2")
    private String inventoryAttribute2;
    @JsonProperty("InventoryTypeId")
    private String inventoryTypeId;
    @JsonProperty("ItemId")
    private String itemId;
    @JsonProperty("ProductStatusId")
    private String productStatusId;
    @JsonProperty("QuantityUomId")
    private String quantityUomId;
    @JsonProperty("RetailPrice")
    private Double retailPrice;
    @JsonProperty("ShippedQuantity")
    private Double shippedQuantity;

    public Asn getAsn() {
        return asn;
    }

    public void setAsn(Asn asn) {
        this.asn = asn;
    }

    public String getAsnLineId() {
        return asnLineId;
    }

    public void setAsnLineId(String asnLineId) {
        this.asnLineId = asnLineId;
    }

    public Object getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Object batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public String getCountryOfOrigin() {
        return countryOfOrigin;
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.countryOfOrigin = countryOfOrigin;
    }

    public Object getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Object expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Extended getExtended() {
        return extended;
    }

    public void setExtended(Extended extended) {
        this.extended = extended;
    }

    public Object getInventoryAttribute1() {
        return inventoryAttribute1;
    }

    public void setInventoryAttribute1(Object inventoryAttribute1) {
        this.inventoryAttribute1 = inventoryAttribute1;
    }

    public String getInventoryAttribute2() {
        return inventoryAttribute2;
    }

    public void setInventoryAttribute2(String inventoryAttribute2) {
        this.inventoryAttribute2 = inventoryAttribute2;
    }

    public String getInventoryTypeId() {
        return inventoryTypeId;
    }

    public void setInventoryTypeId(String inventoryTypeId) {
        this.inventoryTypeId = inventoryTypeId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getProductStatusId() {
        return productStatusId;
    }

    public void setProductStatusId(String productStatusId) {
        this.productStatusId = productStatusId;
    }

    public String getQuantityUomId() {
        return quantityUomId;
    }

    public void setQuantityUomId(String quantityUomId) {
        this.quantityUomId = quantityUomId;
    }

    public Double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(Double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Double getShippedQuantity() {
        return shippedQuantity;
    }

    public void setShippedQuantity(Double shippedQuantity) {
        this.shippedQuantity = shippedQuantity;
    }

}
