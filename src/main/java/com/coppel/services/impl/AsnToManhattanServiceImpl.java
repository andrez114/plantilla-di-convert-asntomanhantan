package com.coppel.services.impl;

import com.coppel.entities.AsnToManhattan;
import com.coppel.repository.asn.AsnToManhattanRepository;
import com.coppel.services.AsnToManhattanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsnToManhattanServiceImpl implements AsnToManhattanService {

    @Autowired
    private  final AsnToManhattanRepository asnToManhattanRepository;

    public AsnToManhattanServiceImpl(AsnToManhattanRepository asnToManhattanRepository) {
        this.asnToManhattanRepository = asnToManhattanRepository;
    }

    @Override
    public void insertAsnId(AsnToManhattan asnToManhattan) {
        asnToManhattanRepository.save(asnToManhattan);
    }
}
