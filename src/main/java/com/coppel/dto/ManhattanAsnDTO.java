package com.coppel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ManhattanAsnDTO {

    private int iduKeyx;
    private String numOriginalOrderId;
    private String desPayload;
    private Date fecRegistro;
}