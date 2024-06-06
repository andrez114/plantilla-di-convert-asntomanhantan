
package com.coppel.dto.jsonout;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Asn {

    @JsonProperty("AsnId")
    private String asnId;

    public Asn(String asnId) {
        this.asnId = asnId;
    }

    public String getAsnId() {
        return asnId;
    }

    public void setAsnId(String asnId) {
        this.asnId = asnId;
    }

}
