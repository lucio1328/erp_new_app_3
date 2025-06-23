package com.lucio.erp_new_app_3.services.salary;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.configs.ErpnextProperties;
import com.lucio.erp_new_app_3.dtos.salary.SalaryStructure;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

import jakarta.servlet.http.HttpSession;

@Service
public class SalaryStructureService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ErpnextProperties erpnextProperties;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public List<SalaryStructure> getAllStructure(String sessionCookie) {
        String endpoint = "/api/resource/Salary Structure?fields=[\"*\"]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(SalaryStructure.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des salary structure", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public ResponseEntity<Map<String, Object>> createSalaryGrid(HttpSession session, SalaryStructure salaryGridDTO) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    Map.of("error", "Session not authenticated"));
        }

        try {
            if(salaryGridDTO.getEarnings()!=null ){
                salaryGridDTO.getEarnings().forEach(c -> c.setType("Earning"));
            }
            if(salaryGridDTO.getDeductions()!=null){
                salaryGridDTO.getDeductions().forEach(c -> c.setType("Deduction"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers = preparationApi.buildApiHeaders();

            HttpEntity<SalaryStructure> request = new HttpEntity<>(salaryGridDTO, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    erpnextProperties.getUrl() + "/api/resource/Salary Structure",
                    HttpMethod.POST,
                    request,
                    String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = objectMapper.readValue(
                        response.getBody(), Map.class);
                @SuppressWarnings("unchecked")
                Map<String, Object> data = (Map<String, Object>) responseBody.get("data");
                String docName = (String) data.get("name");
                System.out.println(docName+"\n");

                return ResponseEntity.ok(responseBody);
            }
            else {
                return ResponseEntity.status(response.getStatusCode()).body(
                        Map.of("error", "Failed to create salary grid"));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("error", e.getMessage()));
        }
    }


    public ResponseEntity<Map<String, Object>> submitSalaryGrid(HttpSession session,String docName) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers = preparationApi.buildApiHeaders();

            Map<String, Object> body = Map.of(
                "doc", Map.of(
                    "doctype", "Salary Structure",
                    "name", docName
                )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                erpnextProperties.getUrl() + "/api/method/frappe.client.submit",
                HttpMethod.POST,
                request,
                String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                @SuppressWarnings("unchecked")
                Map<String, Object> responseBody = objectMapper.readValue(response.getBody(), Map.class);
                return ResponseEntity.ok(responseBody);
            }
            else {
                return ResponseEntity.status(response.getStatusCode())
                        .body(Map.of("error", "Échec de la validation de la grille salariale"));
            }
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
