package com.coppel.dto.asn.muebles;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ASNLine {

	@JsonProperty("AsnLineId")
	private String asnLineId;

	@JsonProperty("BatchNumber")
	private String batchNumber;

	@JsonProperty("Canceled")
	private boolean canceled;

	@JsonProperty("CountryOfOrigin")
	private String countryOfOrigin;

	@JsonProperty("Extended")
	private Extended extended;

	@JsonProperty("ExpiryDate")
	private String expiryDate;

	@JsonProperty("InventoryAttribute1")
	private String inventoryAttribute1;

	@JsonProperty("InventoryAttribute2")
	private String inventoryAttribute2;

	@JsonProperty("InventoryTypeId")
	private String inventoryTypeId;

	@JsonProperty("ItemId")
	private String itemId;

	@JsonProperty("ProductStatusId")
	private String productStatusId;

	@JsonProperty("PurchaseOrderId")
	private String purchaseOrderId;

	@JsonProperty("PurchaseOrderLineId")
	private String purchaseOrderLineId;

	@JsonProperty("QuantityUomId")
	private String quantityUomId;

	@JsonProperty("RetailPrice")
	private double retailPrice;

	@JsonProperty("ShippedQuantity")
	private double shippedQuantity;

	@JsonProperty("VendorId")
	private String vendorId;

	@JsonProperty("InventoryAttribute3")
	private String inventoryAttribute3;
	
	@JsonProperty("AsnId")
	private String asnId;

	@JsonProperty("InventoryAttribute4")
	private String inventoryAttribute4;

	@JsonProperty("InventoryAttribute5")
	private String inventoryAttribute5;
	
}
	
