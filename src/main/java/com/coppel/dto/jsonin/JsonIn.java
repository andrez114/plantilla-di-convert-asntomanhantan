
package com.coppel.dto.jsonin;

import java.util.List;

import com.coppel.dto.purchaseOrder.PurchaseOrderLineDTO;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;

public class JsonIn  implements Serializable {

    @JsonProperty("asnReference")
    private String asnReference;

    @JsonProperty("asnTypeCode")
    private Integer asnTypeCode;
    @JsonProperty("customsPetition")
    private Long customsPetition;
    @JsonProperty("dataTimestamp")
    private String dataTimestamp;
    @JsonProperty("destinationBusinessUnitId")
    private Integer destinationBusinessUnitId;
    @JsonProperty("icdStateCode")
    private Long icdStateCode;
    @JsonProperty("lpns")
    private List<Lpn> lpns;
    @JsonProperty("purchaseOrderId")
    private Long purchaseOrderId;
    @JsonProperty("rfc")
    private String rfc;
    @JsonProperty("sourceBusinessUnitId")
    private Integer sourceBusinessUnitId;
    @JsonProperty("vendorId")
    private String vendorId;
    @JsonProperty("vendorType")
    private Long vendorType;

    private List<PurchaseOrderLineDTO> purchaseOrderLineDTOList;

    public List<PurchaseOrderLineDTO> getPurchaseOrderLineDTOList() {
        return purchaseOrderLineDTOList;
    }

    public JsonIn setPurchaseOrderLineDTOList(List<PurchaseOrderLineDTO> purchaseOrderLineDTOList) {
        this.purchaseOrderLineDTOList = purchaseOrderLineDTOList;
        return this;
    }

    public String getAsnReference() {
        return asnReference;
    }

    public void setAsnReference(String asnReference) {
        this.asnReference = asnReference;
    }

    public Integer getAsnTypeCode() {
        return asnTypeCode;
    }

    public void setAsnTypeCode(Integer asnTypeCode) {
        this.asnTypeCode = asnTypeCode;
    }

    public Long getCustomsPetition() {
        return customsPetition;
    }

    public void setCustomsPetition(Long customsPetition) {
        this.customsPetition = customsPetition;
    }

    public String getDataTimestamp() {
        return dataTimestamp;
    }

    public void setDataTimestamp(String dataTimestamp) {
        this.dataTimestamp = dataTimestamp;
    }

    public Integer getDestinationBusinessUnitId() {
        return destinationBusinessUnitId;
    }

    public void setDestinationBusinessUnitId(Integer destinationBusinessUnitId) {
        this.destinationBusinessUnitId = destinationBusinessUnitId;
    }

    public Long getIcdStateCode() {
        return icdStateCode;
    }

    public void setIcdStateCode(Long icdStateCode) {
        this.icdStateCode = icdStateCode;
    }

    public List<Lpn> getLpns() {
        return lpns;
    }

    public void setLpns(List<Lpn> lpns) {
        this.lpns = lpns;
    }

    public Long getPurchaseOrderId() {
        return purchaseOrderId;
    }

    public void setPurchaseOrderId(Long purchaseOrderId) {
        this.purchaseOrderId = purchaseOrderId;
    }

    public String getRfc() {
        return rfc;
    }

    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    public Integer getSourceBusinessUnitId() {
        return sourceBusinessUnitId;
    }

    public void setSourceBusinessUnitId(Integer sourceBusinessUnitId) {
        this.sourceBusinessUnitId = sourceBusinessUnitId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public Long getVendorType() {
        return vendorType;
    }

    public void setVendorType(Long vendorType) {
        this.vendorType = vendorType;
    }
}
