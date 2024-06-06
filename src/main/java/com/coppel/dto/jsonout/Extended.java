
package com.coppel.dto.jsonout;

import com.fasterxml.jackson.annotation.JsonProperty;



public class Extended {

    @JsonProperty("ComparisonPrice")
    private String comparisonPrice;

    public Extended(String comparisonPrice) {
        this.comparisonPrice = comparisonPrice;
    }

    public String getComparisonPrice() {
        return comparisonPrice;
    }

    public void setComparisonPrice(String comparisonPrice) {
        this.comparisonPrice = comparisonPrice;
    }

}
