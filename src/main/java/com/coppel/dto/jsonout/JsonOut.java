
package com.coppel.dto.jsonout;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public class JsonOut {

    @JsonProperty("Data")
    private List<Datum> data;

    public JsonOut(List<Datum> data) {
        this.data = data;
    }

    public List<Datum> getData() {
        return data;
    }

    public void setData(List<Datum> data) {
        this.data = data;
    }

}
