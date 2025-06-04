package com.lucio.erp_new_app_3.dtos.salary;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SalaryRegisterTotal {

    @JsonProperty("payment_days")
    private Double paymentDaysTest;

    @JsonProperty("indemnite")
    private BigDecimal indemniteTest;

    @JsonProperty("salaire_base")
    private BigDecimal salaireBaseTest;

    @JsonProperty("gross_pay")
    private BigDecimal grossPayTest;

    @JsonProperty("taxe_sociale")
    private BigDecimal taxeSocialeTest;

    @JsonProperty("total_deduction")
    private BigDecimal totalDeductionTest;

    @JsonProperty("net_pay")
    private BigDecimal netPayTest;

}

