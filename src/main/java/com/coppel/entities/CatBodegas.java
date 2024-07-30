package com.coppel.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "catbodegas")
@Data
public class CatBodegas {

    @Id
    @Column(name = "numbodega")
    private Integer numBodega;

    @Column(name = "numbodegamuebles")
    private Integer numBodegaMuebles;

}
