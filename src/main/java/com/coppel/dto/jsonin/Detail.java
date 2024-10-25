
package com.coppel.dto.jsonin;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class Detail implements Serializable{

    @JsonProperty("asnReference")
    private String asnReference;

    @JsonProperty("businessUnitId")
    private Long businessUnitId;

    @JsonProperty("containerTypeId")
    private Long containerTypeId;

    @JsonProperty("currentSaleUnitRetailPriceAmount")
    private Long currentSaleUnitRetailPriceAmount;

    @JsonProperty("descriptionDocument")
    private String descriptionDocument;

    @JsonProperty("documentTypeCode")
    private Long documentTypeCode;

    @JsonProperty("lpnId")
    private String lpnId;

    @JsonProperty("retailUnitCount")
    private Long retailUnitCount;

    @JsonProperty("serializedItem")
    private String serializedItem;

    @JsonProperty("shortDescription")
    private String shortDescription;

    @JsonProperty("sku")
    private String sku;

    @JsonProperty("typeDocumentAsnId")
    private Long typeDocumentAsnId;

    @JsonProperty("typeDocumentId")
    private Long typeDocumentId;

    @JsonProperty("typeShortName")
    private String typeShortName;

    @JsonProperty("refurbishedUnitId")
    private String refurbishedUnitId;

    @JsonProperty("expiryDate")
    private String ExpiryDate;

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getRefurbishedUnitId() {
        return refurbishedUnitId;
    }

    public void setRefurbishedUnitId(String refurbishedUnitId) {
        this.refurbishedUnitId = refurbishedUnitId;
    }

    public String getAsnReference() {
        return asnReference;
    }

    public void setAsnReference(String asnReference) {
        this.asnReference = asnReference;
    }

    public Long getBusinessUnitId() {
        return businessUnitId;
    }

    public void setBusinessUnitId(Long businessUnitId) {
        this.businessUnitId = businessUnitId;
    }

    public Long getContainerTypeId() {
        return containerTypeId;
    }

    public void setContainerTypeId(Long containerTypeId) {
        this.containerTypeId = containerTypeId;
    }

    public Long getCurrentSaleUnitRetailPriceAmount() {
        return currentSaleUnitRetailPriceAmount;
    }

    public void setCurrentSaleUnitRetailPriceAmount(Long currentSaleUnitRetailPriceAmount) {
        this.currentSaleUnitRetailPriceAmount = currentSaleUnitRetailPriceAmount;
    }

    public String getDescriptionDocument() {
        return descriptionDocument;
    }

    public void setDescriptionDocument(String descriptionDocument) {
        this.descriptionDocument = descriptionDocument;
    }

    public Long getDocumentTypeCode() {
        return documentTypeCode;
    }

    public void setDocumentTypeCode(Long documentTypeCode) {
        this.documentTypeCode = documentTypeCode;
    }

    public String getLpnId() {
        return lpnId;
    }

    public void setLpnId(String lpnId) {
        this.lpnId = lpnId;
    }

    public Long getRetailUnitCount() {
        return retailUnitCount;
    }

    public void setRetailUnitCount(Long retailUnitCount) {
        this.retailUnitCount = retailUnitCount;
    }

    public String getSerializedItem() {
        return serializedItem;
    }

    public void setSerializedItem(String serializedItem) {
        this.serializedItem = serializedItem;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Long getTypeDocumentAsnId() {
        return typeDocumentAsnId;
    }

    public void setTypeDocumentAsnId(Long typeDocumentAsnId) {
        this.typeDocumentAsnId = typeDocumentAsnId;
    }

    public Long getTypeDocumentId() {
        return typeDocumentId;
    }

    public void setTypeDocumentId(Long typeDocumentId) {
        this.typeDocumentId = typeDocumentId;
    }

    public String getTypeShortName() {
        return typeShortName;
    }

    public void setTypeShortName(String typeShortName) {
        this.typeShortName = typeShortName;
    }
}
