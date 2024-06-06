package com.coppel.execeptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.coppel.dto.ApiResponseDTO;
import com.coppel.dto.Meta;

import com.coppel.util.MetaGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import static com.coppel.util.AppMessages.*;
import static com.coppel.util.AppMessages.ERROR_TIMEOUT;
import static com.coppel.util.LogFile.*;
import static com.coppel.util.LogFile.logRequest;


/**
 * Clase para manejo de excepciones no controladas.
 */
@ControllerAdvice
public class AppExceptionHandler {

    private final MetaGenerator metaGenerator;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public AppExceptionHandler(MetaGenerator metaGenerator) {
        this.metaGenerator = metaGenerator;
    }


    /*
     * Cualquier excepcion tipo NullPointer que ocurra
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleNullPointer(NullPointerException exception) {
        ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>();

        logExcepcion(exception);

        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.INTERNAL_SERVER_ERROR, ERROR, exception.getMessage()));
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /*
     * Si un paramatro no llega por requestParam
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponseDTO<Void>> handleMissingRequestParam(MissingServletRequestParameterException exception) {
        ApiResponseDTO<Void> apiResponseDTO = new ApiResponseDTO<>();

        logExcepcion(exception);

        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.BAD_REQUEST, ERROR, exception.getMessage()));
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }


    /*
     * Cuando mandas un parametro con tipo de dato incorrecto
     */
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleTypeMismatch(TypeMismatchException exception) {
        ApiResponseDTO<Object> apiResponseDTO = new ApiResponseDTO<>();

        logExcepcion(exception);

        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.BAD_REQUEST, DATA_TYPE_BAD_REQUEST_MESSAGE, exception.getMessage()));
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /*
     * Cuando una o mas regla de RequestBody no se cumple
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException exception) {
        ApiResponseDTO<Object> apiResponseDTO = new ApiResponseDTO<>();
        List<String> listaErrores = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        logExcepcion(exception);

        exception.getAllErrors().forEach(
                objectError -> {
                    listaErrores.add(
                            String.format("%s - %s",
                                    Objects.requireNonNull(objectError.getCodes())[1],
                                    objectError.getDefaultMessage())
                    );

                    sb.append(objectError.getDefaultMessage());
                    sb.append(". ");
                });

        String messageMeta = sb.substring(0, sb.length() - 2);
        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.BAD_REQUEST, messageMeta, exception.getMessage()));
        apiResponseDTO.setData(listaErrores);
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /*
     * Cuando uno o mas campos del body tienen mal formato
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleHttpMessageNotReadable(HttpMessageNotReadableException exception) {
        ApiResponseDTO<Object> apiResponseDTO = new ApiResponseDTO<>();

        logExcepcion(exception);

        apiResponseDTO.setMeta(metaGenerator.crearMetaObject(HttpStatus.BAD_REQUEST, DATA_TYPE_BAD_REQUEST_MESSAGE, exception.getMessage()));
        return new ResponseEntity<>(apiResponseDTO, HttpStatus.BAD_REQUEST);
    }

    /*
     * Cuando un servicio responde con estatus incorrecto
     */
    @ExceptionHandler(HttpClientErrorException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleClientServerErrorResponse(HttpClientErrorException ex) {
        // Responses NOT OK 400-499
        logExcepcion(ex);
        final String responseString = ex.getResponseBodyAsString();
        final JSONObject responseJSON;

        try {
            responseJSON = new JSONObject(responseString);
            if (responseJSON.has("meta")) {
                final Meta meta = objectMapper.readValue(responseJSON.get("meta").toString(), Meta.class);
                logMeta(meta);

                return new ResponseEntity<>(new ApiResponseDTO<>(meta, null), ex.getStatusCode());
            }
        } catch (JSONException | JsonProcessingException e) {
            logExcepcion(e);
        }

        // No cuenta con meta o no se puede castear
        logRequest(ex);
        final Meta meta = metaGenerator.crearMetaObject(ex.getStatusCode(), CLIENT_ERROR, ex.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(meta, null), ex.getStatusCode());
    }

    /*
     * Cuando un servicio responde con estatus de error de servidor
     */
    @ExceptionHandler(HttpServerErrorException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleClientServerErrorResponse(HttpServerErrorException ex) {
        Meta meta;
        // Responses NOT OK 500+
        logExcepcion(ex);
        final String responseString = ex.getResponseBodyAsString();
        final JSONObject responseJSON;
        try {
            responseJSON = new JSONObject(responseString);
            if (responseJSON.has("meta")) {
                meta = objectMapper.readValue(responseJSON.get("meta").toString(), Meta.class);
                logMeta(meta);
            } else {
                // Si no tiene meta
                logRequest(ex);
            }
        } catch (JSONException | JsonProcessingException e) {
            logExcepcion(e);
            // Si no se puede castear a JSON
            logRequest(ex);
        }

        meta = metaGenerator.crearMetaObject(HttpStatus.BAD_GATEWAY, ERROR, ex.getMessage());
        return new ResponseEntity<>(new ApiResponseDTO<>(meta, null), HttpStatus.BAD_GATEWAY);
    }

    /*
     * Cuando un servicio no responde (timeout)
     */
    @ExceptionHandler(ResourceAccessException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleServiceTimeout(ResourceAccessException ex) {
        // Servicio da timeout o no se tiene acceso
        logExcepcion(ex);
        final Meta meta = metaGenerator.crearMetaObject(HttpStatus.GATEWAY_TIMEOUT, ERROR_TIMEOUT, ex.getMessage());

        return new ResponseEntity<>(new ApiResponseDTO<>(meta, null), HttpStatus.GATEWAY_TIMEOUT);
    }


}
