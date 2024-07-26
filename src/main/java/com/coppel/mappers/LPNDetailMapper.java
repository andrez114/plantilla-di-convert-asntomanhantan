package com.coppel.mappers;

import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.Detail;
import com.coppel.dto.purchaseOrder.PurchaseOrderLineDTO;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Setter
public class LPNDetailMapper {

    private static final String DEFAULT_NULL = null;
    private static final String DEFAULT_COUNTRY_OF_ORIGIN = "MEXICO";
    private static final String DEFAULT_INVENTORY_TYPE_ID = "N";
    private static final String DEFAULT_LPN_DETAIL_STATUS = "1000";
    private static final String DEFAULT_PRODUCT_STATUS_ID = "InStock";
    private static final String DEFAULT_QUANTITY_UOM_ID = "UNIT";

    private String purchaseOrderId;
    private String vendorId;
    private AtomicInteger counter = new AtomicInteger(1);
    private List<PurchaseOrderLineDTO> purchaseOrderLineDTOList;

    public ASNMessage.ASNManhattan.LPN.LPNDetail map(Detail source) {

        String comparisonPrice = Objects.nonNull(source.getCurrentSaleUnitRetailPriceAmount()) ?
                source.getCurrentSaleUnitRetailPriceAmount().toString()
                : null;
        ASNMessage.ASNManhattan.LPN.LPNDetail.Extended extended = ASNMessage.ASNManhattan.LPN.LPNDetail.Extended.builder()
                .comparisonPrice(comparisonPrice)
                .build();
        String retailPrice = Objects.nonNull(source.getCurrentSaleUnitRetailPriceAmount()) ?
                source.getCurrentSaleUnitRetailPriceAmount().toString() : null;

        String shippedQuantity = Objects.nonNull(source.getRetailUnitCount()) ?
                source.getRetailUnitCount().toString() : null;
        String purchaseOrderLineId = purchaseOrderLineDTOList.stream()
                .filter(purchaseOrderLineDTO -> purchaseOrderLineDTO.getItemId().equals(source.getSku()))
                .map(PurchaseOrderLineDTO::getPurchaseOrderLineId)
                .findFirst().orElse(null);
        return ASNMessage.ASNManhattan.LPN.LPNDetail.builder()
                .asnLineId(purchaseOrderLineId)
                .batchNumber(DEFAULT_NULL)
                .countryOfOrigin(DEFAULT_COUNTRY_OF_ORIGIN)
                .extended(extended)
                .expiryDate(DEFAULT_NULL)
                .inventoryAttribute1(DEFAULT_NULL)
                .inventoryAttribute2(DEFAULT_NULL)
                .inventoryAttribute3(DEFAULT_NULL)
                .inventoryAttribute4(DEFAULT_NULL)
                .inventoryAttribute5(DEFAULT_NULL)
                .inventoryTypeId(DEFAULT_INVENTORY_TYPE_ID)
                .itemId(source.getSku())
                .lpnDetailId(String.valueOf(counter.getAndIncrement()))
                .lpnDetailStatus(DEFAULT_LPN_DETAIL_STATUS)
                .productStatusId(DEFAULT_PRODUCT_STATUS_ID)
                .purchaseOrderId(purchaseOrderId)
                .purchaseOrderLineId(purchaseOrderLineId)
                .quantityUomId(DEFAULT_QUANTITY_UOM_ID)
                .retailPrice(retailPrice)
                .shippedQuantity(shippedQuantity)
                .vendorId(vendorId)
                .build();
    }

    public List<ASNMessage.ASNManhattan.LPN.LPNDetail> mapAll(List<Detail> source) {
        List<ASNMessage.ASNManhattan.LPN.LPNDetail> mappedList = source.stream().map(this::map).toList();
        counter.set(1);
        return mappedList;
    }
}
