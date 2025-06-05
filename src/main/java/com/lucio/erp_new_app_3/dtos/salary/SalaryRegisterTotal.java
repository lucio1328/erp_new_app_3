package com.lucio.erp_new_app_3.dtos.salary;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class SalaryRegisterTotal {
    private BigDecimal grossPay = BigDecimal.ZERO;
    private BigDecimal totalDeduction = BigDecimal.ZERO;
    private BigDecimal netPay = BigDecimal.ZERO;
    private BigDecimal totalLoanRepayment = BigDecimal.ZERO;

    private Map<String, BigDecimal> composantes = new HashMap<>();
}


