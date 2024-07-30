package com.coppel.repository.bodega;

import com.coppel.entities.CatBodegas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CatBodegasRepository extends JpaRepository<CatBodegas,Integer> {

    CatBodegas findAllBynumBodega(Integer clothingNumber);
}
