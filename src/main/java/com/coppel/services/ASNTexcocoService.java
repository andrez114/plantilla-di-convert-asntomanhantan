package com.coppel.services;

import com.coppel.dto.MerchandisingInfoDTO;
import com.coppel.dto.asn.muebles.ASNMessageMuebles;
import com.coppel.dto.asn.ropa.ASNMessage;
import com.coppel.dto.jsonin.JsonIn;
import com.coppel.dto.purchaseOrder.PurchaseOrderLineDTO;
import com.coppel.execeptions.ErrorGeneralException;

import java.util.List;

public interface ASNTexcocoService {
    void processASNCanonicoRopa(JsonIn asnCanonicoRequest);

    void publishASNToManhattan(ASNMessage asnManhattanMessage);

    void processASNCanonicoMuebles(JsonIn asnCanonicoRequest);

    void publishASNToManhattanMuebles(ASNMessageMuebles asnManhattanMessage);

    List<PurchaseOrderLineDTO> getPurchaseOrderFromManhattan(String purchaseOrder);

    List<MerchandisingInfoDTO> getMerchandisingInfo(List<String> skus) throws ErrorGeneralException;

    void processOrignalOrderRopa(JsonIn originalOrderROpa);

    void insertLog(String asnRefence, Exception error);
    
    void insertManhattanAsn(String payload, String asnReference);
}
