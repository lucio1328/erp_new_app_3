package com.lucio.erp_new_app_3.dtos.salary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SalaryRegister {

    @JsonProperty("salary_slip_id")
    private String salarySlipId;

    @JsonProperty("employee")
    private String employee;

    @JsonProperty("employee_name")
    private String employeeName;

    @JsonProperty("data_of_joining")
    private String dateOfJoining;

    @JsonProperty("branch")
    private String branch;

    @JsonProperty("department")
    private String department;

    @JsonProperty("designation")
    private String designation;

    @JsonProperty("company")
    private String company;

    @JsonProperty("start_date")
    private String startDate;

    @JsonProperty("end_date")
    private String endDate;

    @JsonProperty("leave_without_pay")
    private Double leaveWithoutPay;

    @JsonProperty("absent_days")
    private Double absentDays;

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

    @JsonProperty("total_loan_repayment")
    private BigDecimal totalLoanRepayment;

    @JsonProperty("total_deduction")
    private BigDecimal totalDeduction;

    @JsonProperty("net_pay")
    private BigDecimal netPay;

    @JsonProperty("currency")
    private String currency;
}

