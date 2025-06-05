package com.lucio.erp_new_app_3.dtos.salary;

import java.util.ArrayList;
import java.util.List;

import com.lucio.erp_new_app_3.dtos.data.DataDto;

import lombok.Data;

@Data
public class SalaryTotalsResponse {
    private double totalGrossPay;
    private double totalDeductions;
    private double totalNetPay;
    private String currency;

    private List<Double> componentsSum=new ArrayList<>();
    public SalaryTotalsResponse(List<SalarySlip> salarySlipDtos,List<DataDto> dataDtos) {
        for (int i = 0; i < dataDtos.size(); i++) {
                componentsSum.add(0.0);
        }

        for (SalarySlip salarySlipDto : salarySlipDtos) {
            totalGrossPay+=salarySlipDto.getGrossPay();
            totalDeductions+=salarySlipDto.getTotalDeduction();
            totalNetPay+=salarySlipDto.getNetPay();
            this.currency=salarySlipDto.getCurrency();

            System.out.println();
            System.out.println("Salary Slip: "+ salarySlipDto);
            System.out.println("Total Gross Pay: "+ totalGrossPay);
            System.out.println();

            List<Double> comps = salarySlipDto.getComponentsDef();
            if (comps != null && comps.size() == dataDtos.size()) {
                for (int i = 0; i < comps.size(); i++) {
                    componentsSum.set(i, componentsSum.get(i) + comps.get(i));
                }
            }

        }
    }

}
