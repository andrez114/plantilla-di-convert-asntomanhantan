package com.coppel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "asntomanhattan")
@Data
public class AsnToManhattan {

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "asn_id")
    private String asnId;

    @Column(name = "payload")
    @Basic(optional = false)
    @NotNull
    private String payload;
}
