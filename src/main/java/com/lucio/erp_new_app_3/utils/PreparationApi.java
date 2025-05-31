package com.lucio.erp_new_app_3.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.configs.ErpnextProperties;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
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
            }
            else {
                throw new ErpApiException("Erreur HTTP reçue : " + response.getStatusCode().value(), response.getStatusCode().value());
            }

        }
        catch (ErpApiException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la récupération depuis l'API ERPNext", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public HttpHeaders createHeaders(String sessionCookie) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Cookie", sessionCookie);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
