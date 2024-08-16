package com.coppel.execeptions;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class ErrorGeneralException extends Exception{

    public ErrorGeneralException(String mensaje){
        super(mensaje);
        log.error(mensaje);
    }
}
