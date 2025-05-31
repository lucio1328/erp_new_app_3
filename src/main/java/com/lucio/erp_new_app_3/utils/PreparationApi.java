package com.lucio.erp_new_app_3.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.configs.ErpnextProperties;

public class PreparationApi {
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    private ErpnextProperties erpnextProperties;

    @Autowired
    private ObjectMapper objectMapper;


    public JsonNode getJsonDataFromApi(String endpoint, String sessionCookie) {
        String url = erpnextProperties.getUrl() + endpoint;
        HttpEntity<Void> request = new HttpEntity<>(createHeaders(sessionCookie));

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    request,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                JsonNode root = objectMapper.readTree(response.getBody());
                return root.get("data");
            } else {
                throw new RuntimeException("Erreur HTTP : " + response.getStatusCode());
            }

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération depuis l'API ERPNext", e);
        }
    }

    public HttpHeaders createHeaders(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionCookie);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
