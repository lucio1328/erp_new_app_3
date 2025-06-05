package com.lucio.erp_new_app_3.dtos.data;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DataDto {
    @JsonProperty("name")
    private String name;
}
