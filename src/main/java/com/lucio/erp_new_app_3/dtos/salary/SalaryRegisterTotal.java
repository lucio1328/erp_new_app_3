package com.lucio.erp_new_app_3.dtos.salary;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SalaryRegisterTotal {

    @JsonProperty("payment_days")
    private Double paymentDays;

    @JsonProperty("indemnite")
    private Double indemnite;

    @JsonProperty("salaire_base")
    private Double salaireBase;

    @JsonProperty("gross_pay")
    private Double grossPay;

    @JsonProperty("taxe_sociale")
    private Double taxeSociale;

    @JsonProperty("total_deduction")
    private Double totalDeduction;

    @JsonProperty("net_pay")
    private Double netPay;

}

