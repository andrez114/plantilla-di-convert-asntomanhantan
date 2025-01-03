package com.coppel.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ApiResponseDTO
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseDTO<T> {

    private Meta meta;
    private T data;
    public ApiResponseDTO(Meta meta) {
        this.meta = meta;
    }


}
