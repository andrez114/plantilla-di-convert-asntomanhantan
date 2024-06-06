
package com.coppel.dto.jsonout;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Datum {

    @JsonProperty("AsnId")
    private String asnId;
    @JsonProperty("AsnLevelId")
    private String asnLevelId;
    @JsonProperty("AsnLine")
    private List<AsnLine> asnLine;
    @JsonProperty("AsnOriginTypeId")
    private String asnOriginTypeId;
    @JsonProperty("AsnStatus")
    private String asnStatus;
    @JsonProperty("Canceled")
    private Boolean canceled;
    @JsonProperty("DestinationFacilityId")
    private String destinationFacilityId;
    @JsonProperty("Lpn")
    private List<Object> lpn;
    @JsonProperty("OriginFacilityId")
    private String originFacilityId;
    @JsonProperty("ShippedDate")
    private String shippedDate;
    @JsonProperty("ShippedLpns")
    private Double shippedLpns;
    @JsonProperty("VendorId")
    private Object vendorId;

    public String getAsnId() {
        return asnId;
    }

    public void setAsnId(String asnId) {
        this.asnId = asnId;
    }

    public String getAsnLevelId() {
        return asnLevelId;
    }

    public void setAsnLevelId(String asnLevelId) {
        this.asnLevelId = asnLevelId;
    }

    public List<AsnLine> getAsnLine() {
        return asnLine;
    }

    public void setAsnLine(List<AsnLine> asnLine) {
        this.asnLine = asnLine;
    }

    public String getAsnOriginTypeId() {
        return asnOriginTypeId;
    }

    public void setAsnOriginTypeId(String asnOriginTypeId) {
        this.asnOriginTypeId = asnOriginTypeId;
    }

    public String getAsnStatus() {
        return asnStatus;
    }

    public void setAsnStatus(String asnStatus) {
        this.asnStatus = asnStatus;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public String getDestinationFacilityId() {
        return destinationFacilityId;
    }

    public void setDestinationFacilityId(String destinationFacilityId) {
        this.destinationFacilityId = destinationFacilityId;
    }

    public List<Object> getLpn() {
        return lpn;
    }

    public void setLpn(List<Object> lpn) {
        this.lpn = lpn;
    }

    public String getOriginFacilityId() {
        return originFacilityId;
    }

    public void setOriginFacilityId(String originFacilityId) {
        this.originFacilityId = originFacilityId;
    }

    public String getShippedDate() {
        return shippedDate;
    }

    public void setShippedDate(String shippedDate) {
        this.shippedDate = shippedDate;
    }

    public Double getShippedLpns() {
        return shippedLpns;
    }

    public void setShippedLpns(Double shippedLpns) {
        this.shippedLpns = shippedLpns;
    }

    public Object getVendorId() {
        return vendorId;
    }

    public void setVendorId(Object vendorId) {
        this.vendorId = vendorId;
    }

}
