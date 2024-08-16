package com.coppel.repository.asn;

import com.coppel.entities.ManhattanAsn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManhattanAsnRepository extends JpaRepository<ManhattanAsn, Integer> {

}
