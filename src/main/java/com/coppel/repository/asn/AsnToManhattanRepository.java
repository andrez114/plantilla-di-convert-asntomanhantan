package com.coppel.repository.asn;

import com.coppel.entities.AsnToManhattan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsnToManhattanRepository extends JpaRepository<AsnToManhattan,String> {
}
