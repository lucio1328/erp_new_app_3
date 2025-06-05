package com.lucio.erp_new_app_3.dtos.salary;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
@JsonDeserialize(using = SalaryRegisterDeserializer.class)
public class SalaryRegister {

    private String salarySlipId;
    private String employee;
    private String employeeName;
    private String dateOfJoining;
    private String branch;
    private String department;
    private String designation;
    private String company;
    private LocalDate startDate;
    private LocalDate endDate;

    private Double leaveWithoutPay;
    private Double absentDays;
    private Double paymentDays;

    private BigDecimal grossPay;
    private BigDecimal totalDeduction;
    private BigDecimal netPay;
    private BigDecimal totalLoanRepayment;
    private String currency;

    private Map<String, BigDecimal> composantes = new HashMap<>();
}


