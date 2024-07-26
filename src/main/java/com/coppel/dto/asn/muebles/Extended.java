package com.coppel.dto.asn.muebles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Extended {

	@JsonProperty("ComparisonPrice")
	private String comparisonPrice;
}