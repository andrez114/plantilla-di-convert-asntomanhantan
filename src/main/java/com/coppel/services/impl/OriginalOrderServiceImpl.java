package com.coppel.services.impl;

import com.coppel.entities.OriginalOrder;
import com.coppel.repository.asn.OriginalOrderRepository;
import com.coppel.services.OriginalOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OriginalOrderServiceImpl implements OriginalOrderService {

    @Autowired
    OriginalOrderRepository originalOrderRepository;


    @Override
    public void insertOriginalOrder(OriginalOrder originalOrder) {
        originalOrderRepository.save(originalOrder);
    }
}
