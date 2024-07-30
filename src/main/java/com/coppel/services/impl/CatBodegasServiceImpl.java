package com.coppel.services.impl;

import com.coppel.entities.CatBodegas;
import com.coppel.repository.bodega.CatBodegasRepository;
import com.coppel.services.CatBodegasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CatBodegasServiceImpl implements CatBodegasService {

    @Autowired
    CatBodegasRepository catBodegasRepository;

    @Override
    public CatBodegas getFurnitureNumber(Integer clothingNumber) {
        return catBodegasRepository.findAllBynumBodega(clothingNumber);
    }
}
