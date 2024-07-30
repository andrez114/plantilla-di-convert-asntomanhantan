package com.coppel.repository.asn;

import com.coppel.entities.OriginalOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OriginalOrderRepository extends JpaRepository<OriginalOrder,String> {
}
