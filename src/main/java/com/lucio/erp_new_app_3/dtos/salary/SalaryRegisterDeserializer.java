package com.lucio.erp_new_app_3.dtos.salary;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class SalaryRegisterDeserializer extends StdDeserializer<SalaryRegister> {

    private static final List<String> FIXED_FIELDS = List.of(
        "salary_slip_id", "employee", "employee_name", "data_of_joining", "branch", "department",
        "designation", "company", "start_date", "end_date", "leave_without_pay", "absent_days",
        "payment_days", "gross_pay", "total_deduction", "net_pay", "total_loan_repayment", "currency"
    );

    public SalaryRegisterDeserializer() {
        this(null);
    }

    public SalaryRegisterDeserializer(Class<?> vc) {
        super(vc);
    }

    @SuppressWarnings("deprecation")
    @Override
    public SalaryRegister deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        ObjectMapper mapper = (ObjectMapper) jp.getCodec();
        JsonNode node = mapper.readTree(jp);

        SalaryRegister sr = new SalaryRegister();

        sr.setSalarySlipId(getText(node, "salary_slip_id"));
        sr.setEmployee(getText(node, "employee"));
        sr.setEmployeeName(getText(node, "employee_name"));
        sr.setDateOfJoining(getText(node, "data_of_joining"));
        sr.setBranch(getText(node, "branch"));
        sr.setDepartment(getText(node, "department"));
        sr.setDesignation(getText(node, "designation"));
        sr.setCompany(getText(node, "company"));
        sr.setStartDate(getLocalDate(node, "start_date"));
        sr.setEndDate(getLocalDate(node, "end_date"));

        sr.setLeaveWithoutPay(getDouble(node, "leave_without_pay"));
        sr.setAbsentDays(getDouble(node, "absent_days"));
        sr.setPaymentDays(getDouble(node, "payment_days"));

        sr.setGrossPay(getBigDecimal(node, "gross_pay"));
        sr.setTotalDeduction(getBigDecimal(node, "total_deduction"));
        sr.setNetPay(getBigDecimal(node, "net_pay"));
        sr.setTotalLoanRepayment(getBigDecimal(node, "total_loan_repayment"));
        sr.setCurrency(getText(node, "currency"));

        Map<String, BigDecimal> composantes = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            String key = entry.getKey();
            if (!FIXED_FIELDS.contains(key) && entry.getValue().isNumber()) {
                composantes.put(key, entry.getValue().decimalValue());
            }
        }

        sr.setComposantes(composantes);
        return sr;
    }

    private String getText(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asText() : null;
    }
    private Double getDouble(JsonNode node, String field) {
        return node.has(field) ? node.get(field).asDouble() : null;
    }
    private BigDecimal getBigDecimal(JsonNode node, String field) {
        return node.has(field) ? new BigDecimal(node.get(field).asText()) : null;
    }
    private LocalDate getLocalDate(JsonNode node, String field) {
        if (node.has(field)) {
            try {
                return LocalDate.parse(node.get(field).asText());
            } catch (Exception ignored) {}
        }
        return null;
    }
}

