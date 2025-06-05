package com.lucio.erp_new_app_3.dtos.salary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.services.salary.SalarySlipService;

import lombok.Data;

@Data
public class SalaryReportResponse {
    private List<SalaryRegister> salaryRegisters;
    private List<SalarySlip> salarySlips;
    private SalaryRegisterTotal totals;

    @Autowired
    private static SalarySlipService salarySlipService;

    @SuppressWarnings("removal")
    public static SalaryReportResponse fromJson(JsonNode root, ObjectMapper mapper,String sessionCookie) throws JsonProcessingException, IllegalArgumentException {
        SalaryReportResponse response = new SalaryReportResponse();
        JsonNode resultNode = root.get("result");

        List<SalaryRegister> registers = Arrays.asList(mapper.treeToValue(resultNode.get(0), SalaryRegister.class));
        response.salaryRegisters = registers;

        List<SalarySlip> slips = new ArrayList<>();
        for (SalaryRegister register : registers) {
            String slipId = register.getSalarySlipId();
            if (slipId != null && !slipId.isEmpty()) {
                try {
                    SalarySlip slip = salarySlipService.getSalarySlip(slipId, sessionCookie);
                    slips.add(slip);
                }
                catch (Exception e) {
                    System.err.println("Erreur lors de la récupération de SalarySlip : " + e.getMessage());
                }
            }
        }
        response.salarySlips = slips;

        JsonNode totalsArray = resultNode.get(1);
        SalaryRegisterTotal total = new SalaryRegisterTotal();
        total.setPaymentDays(totalsArray.get(12).asDouble());
        total.setGrossPay(new Double(totalsArray.get(15).asText()));
        total.setTaxeSociale(new Double(totalsArray.get(16).asText()));
        total.setTotalDeduction(new Double(totalsArray.get(18).asText()));
        total.setNetPay(new Double(totalsArray.get(19).asText()));

        response.setTotals(total);
        return response;
    }
}

