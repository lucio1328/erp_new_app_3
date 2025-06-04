package com.lucio.erp_new_app_3.dtos.salary;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SalaryRegisterTotal {

    @JsonProperty("payment_days")
    private Double paymentDays;

    @JsonProperty("indemnite")
    private BigDecimal indemnite;

    @JsonProperty("salaire_base")
    private BigDecimal salaireBase;

    @JsonProperty("gross_pay")
    private BigDecimal grossPay;

    @JsonProperty("taxe_sociale")
    private BigDecimal taxeSociale;

    @JsonProperty("total_deduction")
    private BigDecimal totalDeduction;

    @JsonProperty("net_pay")
    private BigDecimal netPay;

}

