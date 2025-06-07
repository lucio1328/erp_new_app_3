package com.lucio.erp_new_app_3.dtos.salary;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.salary.register.SalaryRegister;
import com.lucio.erp_new_app_3.dtos.salary.register.SalaryRegisterTotal;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlip;
import com.lucio.erp_new_app_3.services.salary.SalarySlipService;

import lombok.Data;

@Data
public class SalaryReportResponse {
    private List<SalaryRegister> salaryRegisters;
    private List<SalarySlip> salarySlips;
    private SalaryRegisterTotal totals;

    public static SalaryReportResponse fromJson(JsonNode root, ObjectMapper mapper, SalarySlipService salarySlipService,String sessionCookie) throws JsonProcessingException, IllegalArgumentException {
        SalaryReportResponse response = new SalaryReportResponse();
        JsonNode resultNode = root.get("result");

        List<SalaryRegister> registers = new java.util.ArrayList<>();

        int size = resultNode.size();
        System.out.println("Taille Node: "+ size);
        for (int i = 0; i < size - 1; i++) {
            JsonNode item = resultNode.get(i);
            SalaryRegister register = mapper.treeToValue(item, SalaryRegister.class);

            String slipId = register.getSalarySlipId();
            if (slipId != null && !slipId.isEmpty()) {
                try {
                    SalarySlip slip = salarySlipService.getSalarySlip(slipId, sessionCookie);
                    register.setSalarySlip(slip);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la récupération de SalarySlip : " + e.getMessage());
                }
            }

            registers.add(register);
        }

        response.setSalaryRegisters(registers);

        JsonNode totalsArray = resultNode.get(size - 1);
        response.setTotals(parseTotals(totalsArray));
        System.out.println("Totals: "+ response.getTotals());

        return response;
    }

    private static SalaryRegisterTotal parseTotals(JsonNode node) {
        System.out.println("Champs disponibles dans le total : " + node.fieldNames().toString());
        SalaryRegisterTotal total = new SalaryRegisterTotal();

        if (node.has("payment_days")) {
            total.setPaymentDays(node.get("payment_days").asDouble());
        }
        if (node.has("gross_pay")) {
            total.setGrossPay(node.get("gross_pay").asDouble());
        }
        if (node.has("taxe_sociale")) {
            total.setTaxeSociale(node.get("taxe_sociale").asDouble());
        }
        if (node.has("total_deduction")) {
            total.setTotalDeduction(node.get("total_deduction").asDouble());
        }
        if (node.has("net_pay")) {
            total.setNetPay(node.get("net_pay").asDouble());
        }

        return total;
    }
}

