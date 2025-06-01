package com.lucio.erp_new_app_3.dtos.company;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Company {
    private String name;

    @JsonProperty("company_name")
    private String companyName;

    private String abbr;

    @JsonProperty("default_currency")
    private String defaultCurrency;

    private String country;
}
