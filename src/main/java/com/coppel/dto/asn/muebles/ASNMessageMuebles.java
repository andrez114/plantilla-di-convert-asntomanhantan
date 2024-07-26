package com.coppel.dto.asn.muebles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ASNMessageMuebles {

	@JsonProperty("Data")
	private List<ASNManhattan> data;
	
}
