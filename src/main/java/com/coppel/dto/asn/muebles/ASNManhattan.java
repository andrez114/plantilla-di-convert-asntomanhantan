package com.coppel.dto.asn.muebles;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ASNManhattan {

	@JsonProperty("AsnId")
	private String asnId;

	@JsonProperty("AsnLevelId")
	private String asnLevelId;

	@JsonProperty("AsnOriginTypeId")
	private String asnOriginTypeId;

	@JsonProperty("AsnStatus")
	private String asnStatus;

	@JsonProperty("Canceled")
	private boolean canceled;

	@JsonProperty("DestinationFacilityId")
	private String destinationFacilityId;

	@JsonProperty("AsnLine")
	private List<ASNLine> asnLine;

	@JsonProperty("OriginFacilityId")
	private String originFacilityId;

	@JsonProperty("ShippedDate")
	private String shippedDate;

	@JsonProperty("ShippedLpns")
	private double shippedLpns;

	@JsonProperty("VendorId")
	private String vendorId;
	
}
