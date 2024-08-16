package com.coppel.dto.jsonout;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LpnOut {
    @JsonProperty("LpnId")
    private String lpnId;
    @JsonProperty("LpnSizeTypeId")
    private String lpnSizeTypeId;
   @JsonProperty("LpnDetail")
    private List<LpnDetail> details;
    @JsonProperty("PhysicalEntityCodeId")
    private String physicalEntityCodeId;
}
