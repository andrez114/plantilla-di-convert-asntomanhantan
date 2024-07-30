package com.coppel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "originalorderpreviousmanhattan")
@Data
public class OriginalOrder {
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "original_order_id")
    private String originalOrderId;

    @Column(name = "payload")
    @Basic(optional = false)
    @NotNull
    private String payload;
}
