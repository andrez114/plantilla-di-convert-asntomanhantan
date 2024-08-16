package com.coppel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LogAsnManhattanDTO {

    private int iduKeyx;
    private String desType;
    private String desPayload;
    private String desMotivo;
    private Date fecRegistro;
}
