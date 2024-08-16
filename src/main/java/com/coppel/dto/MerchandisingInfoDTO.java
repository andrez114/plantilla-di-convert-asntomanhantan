package com.coppel.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MerchandisingInfoDTO {

    @JsonProperty("Messages")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private String messages;

    @JsonProperty("ItemId")
    private String sku;

    @JsonProperty("MerchandizingGroup")
    private String merchandiseGroupId;

    @JsonProperty("MerchandizingType")
    private String merchandiseTypeId;

    @JsonProperty("UnitCost")
    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    private BigDecimal unitCost;

    @JsonProperty("MerchandisingDepartment")
    private String merchandisingDepartmentId;
}