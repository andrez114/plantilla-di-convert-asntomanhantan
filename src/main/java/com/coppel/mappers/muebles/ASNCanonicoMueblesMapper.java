package com.coppel.mappers.muebles;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import com.coppel.dto.asn.muebles.ASNLine;
import com.coppel.dto.asn.muebles.ASNManhattan;
import com.coppel.dto.asn.muebles.Extended;
import com.coppel.dto.jsonin.Detail;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.jsonin.Lpn;
import com.coppel.dto.purchaseOrder.PurchaseOrderLineDTO;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ASNCanonicoMueblesMapper {

	private static final String DEFAULT_ASN_LEVEL_ID = "ITEM";
	private static final String DEFAULT_ORIGIN_TYPE_ID = "P";
	private static final String DEFAULT_ASN_STATUS = "1000";
	private static final boolean DEFAULT_CANCELED = false;
	private static final String DEAFULT_DESTINATION_FACILITY_ID = "TEXCOCO";
	private static final double DEFAULT_SHIPPED_LPNS = 1.0;

	private static final String DEFAULT_BATCH_NUMBER = null;
	private static final String DEFAULT_COUNTRY_ORIGEN = "MEXICO";
	private static final String DEFAULT_EXPIRY_DATE = null;
	private static final String DEFAULT_INVENTORY_ATTRIBUTE1 = null;
	private static final String DEFAULT_INVENTORY_ATTRIBUTE2 = "N";
	private static final String DEFAULT_INVENTORY_TYPE_ID = "N";
	private static final String DEFAULT_PRODUCT_STATUS_ID = "InStock";
	private static final String DEFAULT_QUANTITY_UOM_ID = "UNIT";
	private static final String DEFAULT_INVENTORY_ATTRIBUTE3 = null;
	private static final String DEFAULT_INVENTORY_ATTRIBUTE4 = null;
	private static final String DEFAULT_INVENTORY_ATTRIBUTE5 = null;

	private static final DateTimeFormatter DATE_TIME_FORMATER = DateTimeFormatter
			.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

	public ASNManhattan toASNManhattan(JsonIn source) {
		ASNManhattan asn = new ASNManhattan();

		asn.setAsnId(source.getAsnReference());
		asn.setAsnLevelId(DEFAULT_ASN_LEVEL_ID);
		asn.setAsnOriginTypeId(DEFAULT_ORIGIN_TYPE_ID);
		asn.setAsnStatus(DEFAULT_ASN_STATUS);
		asn.setCanceled(DEFAULT_CANCELED);
		asn.setDestinationFacilityId(DEAFULT_DESTINATION_FACILITY_ID);

		asn.setAsnLine(getListAsnLine(source));

		asn.setOriginFacilityId(DEAFULT_DESTINATION_FACILITY_ID);
		asn.setShippedDate(LocalDateTime.now().format(DATE_TIME_FORMATER));
		asn.setShippedLpns(DEFAULT_SHIPPED_LPNS);
		asn.setVendorId("PM" + source.getVendorId());

		return asn;

	}

	public List<ASNLine> getListAsnLine(JsonIn source) {
		List<ASNLine> asnLineList = new ArrayList<>();

		Lpn lpn = source.getLpns().get(0);
		int lineidsize = 0;
		for (Detail det : lpn.getDetails()) {

			String purchaseOrderLineId = source.getPurchaseOrderLineDTOList().stream()
					.filter(purchaseOrderLineDTO -> purchaseOrderLineDTO.getItemId().equals(det.getSku()))
					.map(PurchaseOrderLineDTO::getPurchaseOrderLineId).findFirst().orElse(null);

			ASNLine asnLine = new ASNLine();
			asnLine.setAsnLineId(Integer.toString(++lineidsize));
			asnLine.setBatchNumber(DEFAULT_BATCH_NUMBER);
			asnLine.setCanceled(DEFAULT_CANCELED);
			asnLine.setCountryOfOrigen(DEFAULT_COUNTRY_ORIGEN);

			asnLine.setExtended(getExtended(det));

			asnLine.setExpiryDate(DEFAULT_EXPIRY_DATE);
			asnLine.setInventoryAttribute1(DEFAULT_INVENTORY_ATTRIBUTE1);
			asnLine.setInventoryAttribute2(DEFAULT_INVENTORY_ATTRIBUTE2);
			asnLine.setInventoryTypeId(DEFAULT_INVENTORY_TYPE_ID);
			asnLine.setItemId(det.getSku());
			asnLine.setProductStatusId(DEFAULT_PRODUCT_STATUS_ID);
			asnLine.setPurchaseOrderId(source.getPurchaseOrderId().toString());
			asnLine.setPurchaseOrderLineId(purchaseOrderLineId);
			asnLine.setQuantityUomId(DEFAULT_QUANTITY_UOM_ID);
			asnLine.setRetailPrice(det.getCurrentSaleUnitRetailPriceAmount());
			asnLine.setShippedQuantity(det.getRetailUnitCount());
			asnLine.setVendorId("PM" + source.getVendorId());
			asnLine.setInventoryAttribute3(DEFAULT_INVENTORY_ATTRIBUTE3);
			asnLine.setAsnId(source.getAsnReference());
			asnLine.setInventoryAttribute4(DEFAULT_INVENTORY_ATTRIBUTE4);
			asnLine.setInventoryAttribute5(DEFAULT_INVENTORY_ATTRIBUTE5);

			asnLineList.add(asnLine);
		}
		return asnLineList;
	}

	public Extended getExtended(Detail det) {
		Extended ext = new Extended();
		ext.setComparisonPrice(det.getCurrentSaleUnitRetailPriceAmount().toString());
		return ext;
	}
}