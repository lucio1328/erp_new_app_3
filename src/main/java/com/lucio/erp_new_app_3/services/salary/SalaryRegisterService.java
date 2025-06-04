package com.lucio.erp_new_app_3.services.salary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.salary.SalaryReportResponse;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SalaryRegisterService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public SalaryRegisterService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public SalaryReportResponse getSalaryRegisterReport(String sessionCookie) {
        try {
            String url = "/api/method/frappe.desk.query_report.run";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("report_name", "Salary Register");

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            JsonNode response = preparationApi.postJsonDataToApiWithMessage(url, jsonBody, sessionCookie);
            return SalaryReportResponse.fromJson(response, objectMapper);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la récupération du rapport Salary Register",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }
}
