package com.lucio.erp_new_app_3.dtos.salary;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;

@Data
public class SalaryReportResponse {
    private List<SalaryRegister> salaryRegisters;
    private SalaryRegisterTotal totals;

    @SuppressWarnings("removal")
    public static SalaryReportResponse fromJson(JsonNode root, ObjectMapper mapper) throws JsonProcessingException, IllegalArgumentException {
        SalaryReportResponse response = new SalaryReportResponse();
        JsonNode resultNode = root.get("result");

        response.salaryRegisters = Arrays.asList(mapper.treeToValue(resultNode.get(0), SalaryRegister.class));

        JsonNode totalsArray = resultNode.get(1);
        SalaryRegisterTotal total = new SalaryRegisterTotal();
        total.setPaymentDays(totalsArray.get(12).asDouble());
        total.setIndemnite(new Double(totalsArray.get(13).asText()));
        total.setSalaireBase(new Double(totalsArray.get(14).asText()));
        total.setGrossPay(new Double(totalsArray.get(15).asText()));
        total.setTaxeSociale(new Double(totalsArray.get(16).asText()));
        total.setTotalDeduction(new Double(totalsArray.get(18).asText()));
        total.setNetPay(new Double(totalsArray.get(19).asText()));

        response.setTotals(total);
        return response;
    }
}

