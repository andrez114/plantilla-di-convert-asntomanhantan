package com.coppel.dto.asn.ropa;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ASNMessage {

    @JsonProperty("Data")
    private List<ASNManhattan> data;

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class ASNManhattan {
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
        @JsonProperty("Lpn")
        private List<LPN> lpn;
        @JsonProperty("OriginFacilityId")
        private String originFacilityId;
        @JsonProperty("ShippedDate")
        private String shippedDate;
        @JsonProperty("ShippedLpns")
        private Long shippedLpns;
        @JsonProperty("VendorId")
        private String vendorId;


        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        @Getter
        @Setter
        public static class LPN {
            @JsonProperty("AsnId")
            private String asnId;
            @JsonProperty("Canceled")
            private boolean canceled;
            @JsonProperty("LpnDetail")
            private List<LPNDetail> lpnDetail;
            @JsonProperty("LpnId")
            private String lpnId;
            @JsonProperty("LpnSizeTypeId")
            private String lpnSizeTypeId;
            @JsonProperty("LpnStatus")
            private String lpnStatus;
            @JsonProperty("LpnTypeId")
            private String lpnTypeId;
            @JsonProperty("PhysicalEntityCodeId")
            private String physicalEntityCodeId;
            @JsonProperty("PurchaseOrderId")
            private String purchaseOrderId;
            @JsonProperty("VendorId")
            private String vendorId;

            @AllArgsConstructor
            @NoArgsConstructor
            @Builder
            @Getter
            @Setter
            public static class LPNDetail {
                @JsonProperty("AsnLineId")
                private String asnLineId;
                @JsonProperty("BatchNumber")
                private String batchNumber;
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
                @JsonProperty("InventoryAttribute3")
                private String inventoryAttribute3;
                @JsonProperty("InventoryAttribute4")
                private String inventoryAttribute4;
                @JsonProperty("InventoryAttribute5")
                private String inventoryAttribute5;
                @JsonProperty("InventoryTypeId")
                private String inventoryTypeId;
                @JsonProperty("ItemId")
                private String itemId;
                @JsonProperty("LpnDetailId")
                private String lpnDetailId;
                @JsonProperty("LpnDetailStatus")
                private String lpnDetailStatus;
                @JsonProperty("ProductStatusId")
                private String productStatusId;
                @JsonProperty("PurchaseOrderId")
                private String purchaseOrderId;
                @JsonProperty("PurchaseOrderLineId")
                private String purchaseOrderLineId;
                @JsonProperty("QuantityUomId")
                private String quantityUomId;
                @JsonProperty("RetailPrice")
                private String retailPrice;
                @JsonProperty("ShippedQuantity")
                private String shippedQuantity;
                @JsonProperty("VendorId")
                private String vendorId;

                @AllArgsConstructor
                @NoArgsConstructor
                @Builder
                @Getter
                @Setter
                public static class Extended {
                    @JsonProperty("ComparisonPrice")
                    private String comparisonPrice;
                }
            }
        }
    }
}
