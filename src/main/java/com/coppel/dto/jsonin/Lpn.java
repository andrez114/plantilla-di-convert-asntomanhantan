
package com.coppel.dto.jsonin;

import java.io.Serializable;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Lpn implements Serializable {

    @JsonProperty("asnReference")
    private String asnReference;

    @JsonProperty("containerTypeId")
    private Long containerTypeId;

    @JsonProperty("details")
    private List<Detail> details;

    @JsonProperty("lpnId")
    private String lpnId;

    @JsonProperty("totalUnitCount")
    private Long totalUnitCount;

    @JsonProperty("sourceBusinessUnitId")
    private int sourceBusinessUnitId;

    @JsonProperty("destinationBusinessUnitId")
    private int destinationBusinessUnitId;

    public int getSourceBusinessUnitId() {
        return sourceBusinessUnitId;
    }

    public void setSourceBusinessUnitId(int sourceBusinessUnitId) {
        this.sourceBusinessUnitId = sourceBusinessUnitId;
    }

    public int getDestinationBusinessUnitId() {
        return destinationBusinessUnitId;
    }

    public void setDestinationBusinessUnitId(int destinationBusinessUnitId) {
        this.destinationBusinessUnitId = destinationBusinessUnitId;
    }

    public String getAsnReference() {
        return asnReference;
    }

    public void setAsnReference(String asnReference) {
        this.asnReference = asnReference;
    }

    public Long getContainerTypeId() {
        return containerTypeId;
    }

    public void setContainerTypeId(Long containerTypeId) {
        this.containerTypeId = containerTypeId;
    }

    public List<Detail> getDetails() {
        return details;
    }

    public void setDetails(List<Detail> details) {
        this.details = details;
    }

    public String getLpnId() {
        return lpnId;
    }

    public void setLpnId(String lpnId) {
        this.lpnId = lpnId;
    }

    public Long getTotalUnitCount() {
        return totalUnitCount;
    }

    public void setTotalUnitCount(Long totalUnitCount) {
        this.totalUnitCount = totalUnitCount;
    }
}
