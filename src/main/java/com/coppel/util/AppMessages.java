package com.coppel.util;


/**
 * AppMessages
 */
public class AppMessages {

    private AppMessages() {
        throw new IllegalStateException("No existe un constructor para la clase AppMessages");
    }

    //200
    public static final String OK_STATUS_HEALTH = "Servicio xx se encuentra arriba";
    //400-409
    public static final String CLIENT_ERROR = "Ocurrió un error al consumir el servicio.";

    public static final String ERROR = "SERVER_ERROR";
    public static final String UNAUTHORISED_MESSAGE = "Usted no está autorizado para acceder este recurso.";
    public static final String DATA_TYPE_BAD_REQUEST_MESSAGE = "Verifique los datos enviados";

    //500
    public static final String ERROR_TIMEOUT = "Ocurrió un error inesperado. El servidor no dió respuesta";
    public static final String FORMAT_ERROR_PAYLOAD = "Ocurrió un error al intentar mappear el mensaje de Pub/Sub, la esctructura no corresponde a la deseada.";

}