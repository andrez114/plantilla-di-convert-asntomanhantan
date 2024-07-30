package com.coppel.services;

import com.coppel.dto.asn.muebles.ASNMessageMuebles;
import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.purchaseOrder.PurchaseOrderLineDTO;

import java.util.List;

public interface ASNTexcocoService {
    void processASNCanonicoRopa(JsonIn asnCanonicoRequest);

    void publishASNToManhattan(ASNMessage asnManhattanMessage);

    void processASNCanonicoMuebles(JsonIn asnCanonicoRequest);

    void publishASNToManhattanMuebles(ASNMessageMuebles asnManhattanMessage);

    List<PurchaseOrderLineDTO> getPurchaseOrderFromManhattan(String purchaseOrder);

    void processOrignalOrderRopa(JsonIn originalOrderROpa);

}
