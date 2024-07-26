package com.coppel.mappers;

import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.jsonin.Lpn;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
@AllArgsConstructor
public class ASNCanonicoMapper {
    private static final String DEFAULT_ASN_LEVEL_ID = "LPN";
    private static final String DEFAULT_ORIGIN_TYPE_ID = "P";
    private static final String DEFAULT_ASN_STATUS = "1000";
    private static final boolean DEFAULT_IS_CANCELED = true;
    private static final String TEXCOCO_FACILITY_ID = "TEXCOCO";
    private static final DateTimeFormatter DATE_TIME_FORMATER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private LPNMapper lpnMapper;

    public ASNMessage.ASNManhattan toASNManhattan(JsonIn source) {
        Long shippedLpns = source.getLpns().stream()
                .map(Lpn::getLpnId)
                .filter(Objects::nonNull)
                .count();
        lpnMapper.setVendorId(source.getVendorId());
        lpnMapper.setPurchaseOrderId(Objects.nonNull(source.getPurchaseOrderId()) ?
                String.valueOf(source.getPurchaseOrderId()) : null);
        lpnMapper.setPurchaseOrderLineDTOList(source.getPurchaseOrderLineDTOList());
        return ASNMessage.ASNManhattan.builder()
                .asnId(source.getAsnReference())
                .asnLevelId(DEFAULT_ASN_LEVEL_ID)
                .asnOriginTypeId(DEFAULT_ORIGIN_TYPE_ID)
                .asnStatus(DEFAULT_ASN_STATUS)
                .canceled(DEFAULT_IS_CANCELED)
                .destinationFacilityId(TEXCOCO_FACILITY_ID)
                .lpn(lpnMapper.mapAll(source.getLpns()))
                .originFacilityId(TEXCOCO_FACILITY_ID)
                .shippedDate(LocalDateTime.now().format(DATE_TIME_FORMATER))
                .shippedLpns(shippedLpns)
                .vendorId(source.getVendorId())
                .build();
    }
}
