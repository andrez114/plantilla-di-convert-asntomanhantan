package com.coppel.util;

import com.coppel.dto.Meta;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Arrays;

public class LogFile {

    private LogFile(){ throw new IllegalStateException("Utility class"); }

    private static final Logger LOGGER = LogManager.getLogger(LogFile.class.getName());

    private static final String SEPARADOR = "--------------------------------------------------";

    /*
     * Método para grabar en log cualquier excepción ocurrida con su detalle y origin
     */
    public static void logExcepcion(Exception exception){
        String[] classSplit = exception.getClass().getName().split("\\.");
        String clase = classSplit[classSplit.length-1];
        String mensajeLog;

        LOGGER.error(SEPARADOR);
        mensajeLog = String.format("%s: %s", clase,exception.getMessage());
        LOGGER.error(mensajeLog);
        // Obtener origin del error
        StackTraceElement[] st =
                Arrays.stream(exception.getStackTrace())
                        .filter(stackTraceElement -> stackTraceElement.getClassName().startsWith("com.coppel"))
                        .toArray(StackTraceElement[]::new);
        StackTraceElement ste;
        int stackTraceCount = Math.min(st.length, 3);
        LOGGER.error("Stack trace excepcion originada");
        for (int i = 0; i < stackTraceCount; i++) {
            ste = st[i];
            mensajeLog = String.format("%d: [Archivo: %s | Metodo: %s | Linea: %d]", i+1, ste.getFileName(), ste.getMethodName(), ste.getLineNumber());
            LOGGER.error(mensajeLog);
        }
        LOGGER.error(SEPARADOR);
    }

    /*
     * Método para grabar en log algún error sin necesidad de una excepción
     */
    public static void logError(String error){
        LOGGER.error(SEPARADOR);
        LOGGER.error(error);
        LOGGER.error(SEPARADOR);
    }

    /*
     * Método para grabar en log los datos de un objeto META
     */
    public static void logMeta(Meta meta){
        String mensajeLog;

        LOGGER.info(SEPARADOR);

        LOGGER.info("Peticion");
        mensajeLog = String.format("ID de la peticion: %s", meta.getTransactionID());
        LOGGER.info(mensajeLog);
        mensajeLog = String.format("El servicio respondio con un status: %d | %s", meta.getStatusCode(), meta.getStatus());
        LOGGER.info(mensajeLog);
        mensajeLog = String.format("Fecha y hora de peticion: %s", meta.getTimestamp());
        LOGGER.info(mensajeLog);

        if (meta.getMessage() != null && !meta.getMessage().equals("")){
            mensajeLog = String.format("Mensaje: %s", meta.getMessage());
            LOGGER.info(mensajeLog);
        }
        if (meta.getDevMessage() != null && !meta.getDevMessage().equals("")){
            mensajeLog = String.format("Detalle: %s", meta.getDevMessage());
            LOGGER.info(mensajeLog);
        }

        LOGGER.info(SEPARADOR);
    }

    /*
     * Método para grabar en log cuando se manda a llamar el servicio de manera incorrecta
     */
    public static void logRequest(HttpStatusCodeException ex) {
        String mensajeLog;
        LOGGER.warn(SEPARADOR);

        LOGGER.warn("Peticion no exitosa.");
        mensajeLog = String.format("Status code: %s", ex.getStatusCode());
        LOGGER.warn(mensajeLog);
        mensajeLog = String.format("Response string: [%s]", ex.getResponseBodyAsString());
        LOGGER.warn(mensajeLog);

        LOGGER.warn(SEPARADOR);
    }

}
