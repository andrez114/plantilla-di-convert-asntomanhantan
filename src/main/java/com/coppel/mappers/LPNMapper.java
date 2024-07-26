package com.coppel.mappers;

import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.Lpn;
import com.coppel.dto.purchaseOrder.PurchaseOrderLineDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
@RequiredArgsConstructor
@Setter
public class LPNMapper {

    private static final boolean DEFAULT_IS_CANCELED = false;
    private static final String DEFAULT_NULL = null;
    private static final String DEFAULT_MIL = "1000";
    private static final String DEFAULT_ILPN = "iLPN";
    @Setter(AccessLevel.NONE)
    private final LPNDetailMapper lpnDetailMapper;
    private String purchaseOrderId;
    private String vendorId;
    private List<PurchaseOrderLineDTO> purchaseOrderLineDTOList;


    public ASNMessage.ASNManhattan.LPN map(Lpn source) {
        lpnDetailMapper.setPurchaseOrderId(purchaseOrderId);
        lpnDetailMapper.setVendorId(vendorId);
        lpnDetailMapper.setPurchaseOrderLineDTOList(purchaseOrderLineDTOList);
        return ASNMessage.ASNManhattan.LPN.builder()
                .asnId(source.getAsnReference())
                .canceled(DEFAULT_IS_CANCELED)
                .lpnDetail(lpnDetailMapper.mapAll(source.getDetails()))
                .lpnId(source.getLpnId())
                .lpnSizeTypeId(DEFAULT_NULL)
                .lpnStatus(DEFAULT_MIL)
                .lpnTypeId(DEFAULT_ILPN)
                .physicalEntityCodeId(DEFAULT_ILPN)
                .purchaseOrderId(purchaseOrderId)
                .vendorId(vendorId)
                .build();
    }

    public List<ASNMessage.ASNManhattan.LPN> mapAll(List<Lpn> lpns) {
        return lpns.stream().map(this::map).toList();
    }
}
