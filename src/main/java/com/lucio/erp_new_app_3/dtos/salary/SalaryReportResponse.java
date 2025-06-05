package com.lucio.erp_new_app_3.dtos.salary;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class SalaryReportResponse {
    private List<SalaryRegister> salaryRegisters;
    private SalaryRegisterTotal totals;

    public static SalaryReportResponse fromJson(JsonNode root, ObjectMapper mapper) throws JsonProcessingException {
        SalaryReportResponse response = new SalaryReportResponse();
        JsonNode resultNode = root.get("result");

        response.salaryRegisters = Arrays.asList(mapper.treeToValue(resultNode.get(0), SalaryRegister[].class));

        SalaryRegisterTotal total = calculTotals(response.salaryRegisters);
        response.setTotals(total);

        return response;
    }

    public static SalaryRegisterTotal calculTotals(List<SalaryRegister> salaryRegisters) {
        SalaryRegisterTotal total = new SalaryRegisterTotal();

        for (SalaryRegister sr : salaryRegisters) {
            if (sr.getGrossPay() != null)
                total.setGrossPay(total.getGrossPay().add(sr.getGrossPay()));

            if (sr.getTotalDeduction() != null)
                total.setTotalDeduction(total.getTotalDeduction().add(sr.getTotalDeduction()));

            if (sr.getNetPay() != null)
                total.setNetPay(total.getNetPay().add(sr.getNetPay()));

            if (sr.getTotalLoanRepayment() != null)
                total.setTotalLoanRepayment(total.getTotalLoanRepayment().add(sr.getTotalLoanRepayment()));

            for (Map.Entry<String, BigDecimal> entry : sr.getComposantes().entrySet()) {
                total.getComposantes().merge(entry.getKey(), entry.getValue(), BigDecimal::add);
            }
        }

        return total;
    }
}
