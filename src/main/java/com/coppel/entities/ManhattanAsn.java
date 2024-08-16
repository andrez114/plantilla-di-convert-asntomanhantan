package com.coppel.entities;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "ctl_manhattanasn")
@XmlRootElement
@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManhattanAsn {

    @Id
    @Column(nullable = false, name = "idu_keyx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iduKeyx;

    @Column(nullable = false, name = "asn_reference")
    private String asnReference;

    @Column(nullable = false, name = "des_payloadjson", columnDefinition = "TEXT")
    private String desPayload;

    @Column(nullable = false, name = "fec_registro")
    private Date fecRegistro;
}
