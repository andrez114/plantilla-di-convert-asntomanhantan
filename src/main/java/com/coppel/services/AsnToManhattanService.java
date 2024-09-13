package com.coppel.services;

import com.coppel.dto.asn.muebles.ASNMessageMuebles;
import com.coppel.entities.AsnToManhattan;

public interface AsnToManhattanService {

    void insertAsnId(AsnToManhattan asnToManhattan);
    void publishToManhattan(ASNMessageMuebles asnMessageMuebles);
}
