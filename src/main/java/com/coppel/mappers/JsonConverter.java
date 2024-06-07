package com.coppel.mappers;

import com.coppel.util.ServerException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public  <T> T fromJson(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonParseException e) {
            throw new ServerException("JSON mal formateado, verifique el formato: " + e.getOriginalMessage(), null);
        } catch (JsonMappingException e) {
            throw new ServerException("Error en el mapeo del JSON al objeto: " + e.getMessage(), e);
        } catch (IOException e) {
            throw new ServerException("Error de entrada/salida al manejar JSON: " + e.getMessage(), e);
        }
    }

    public  String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new ServerException("Error al convertir objeto a JSON", e);
        }
    }
}