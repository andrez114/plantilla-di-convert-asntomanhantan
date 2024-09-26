package com.coppel.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "AsnManhattanRequestAndRespose")
@Data
public class AsnManhattanRequestAndRespose{

    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "request")
    private String request;

    @Column(name = "response")
    @Basic(optional = false)
    @NotNull
    private String response;

    @Column(name = "asn_id")
    @Basic(optional = false)
    @NotNull
    private String asnID;

    @Column(name = "status_http")
    @Basic(optional = false)
    @NotNull
    private String statusHttp;
}
