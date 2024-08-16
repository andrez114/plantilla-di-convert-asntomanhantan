package com.coppel.repository.asn;

import com.coppel.entities.LogAsnManhattan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogAsnManhattanRepository extends JpaRepository<LogAsnManhattan, Integer> {
}
