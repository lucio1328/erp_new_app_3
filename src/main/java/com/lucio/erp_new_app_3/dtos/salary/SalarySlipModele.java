package com.lucio.erp_new_app_3.dtos.salary;

import java.util.List;

import lombok.Data;

@Data
public class SalarySlipModele {
    private String mois;
    private double grossPay;
    private double totalDeduction;
    private double netPay;
    private List<Double> componentsDef;

    public SalarySlipModele(String mois, double grossPay, double totalDeduction, double netPay,
            List<Double> componentsDef) {
        this.mois = mois;
        this.grossPay = grossPay;
        this.totalDeduction = totalDeduction;
        this.netPay = netPay;
        this.componentsDef = componentsDef;
    }

}
