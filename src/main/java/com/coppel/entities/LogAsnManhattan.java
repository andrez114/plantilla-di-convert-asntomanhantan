package com.coppel.entities;
import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "ctl_logasnmanhattan")
@XmlRootElement
@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogAsnManhattan {

    @Id
    @Column(nullable = false, name = "idu_keyx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int iduKeyx;

    @Column(nullable = false, name = "des_type")
    private String desType = "asn";

    @Column(nullable = false, name = "des_payload", columnDefinition = "TEXT")
    private String desPayload;

    @Column(nullable = false, name = "des_motivo")
    private String desMotivo;

    @Column(nullable = false, name = "fec_registro")
    private Date fecRegistro;
}