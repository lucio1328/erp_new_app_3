package com.lucio.erp_new_app_3.dtos.salary;

import lombok.Data;

@Data
public class SalarySummaryTotals {
    private double totalGrossPay;
    private double totalDeductions;
    private double totalNetPay;
    private double totalCtc;
}
