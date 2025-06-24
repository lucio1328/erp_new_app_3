package com.lucio.erp_new_app_3.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.configs.ErpnextProperties;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;

import java.util.Collections;

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

    public void deleteDataFromApi(String endpoint, String sessionCookie) {
        String url = erpnextProperties.getUrl() + endpoint;
        HttpEntity<Void> request = new HttpEntity<>(createHeaders(sessionCookie));

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.DELETE,
                    request,
                    String.class
            );

            if (response.getStatusCode() != HttpStatus.OK && response.getStatusCode() != HttpStatus.NO_CONTENT) {
                throw new ErpApiException("Erreur HTTP lors de la suppression : " + response.getStatusCode().value(),
                        response.getStatusCode().value());
            }
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la suppression depuis l'API ERPNext",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public JsonNode getJsonDataFromApi(String endpoint, String sessionCookie) {
        String url = erpnextProperties.getUrl() + endpoint;
        HttpEntity<Void> request = new HttpEntity<>(buildApiHeaders());

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

    public HttpHeaders buildApiHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String token = "token " + erpnextProperties.getKey() + ":" + erpnextProperties.getSecret();
        headers.set("Authorization", token);

        return headers;
    }

    public JsonNode postJsonDataToApi(String endpoint, String jsonBody, String sessionCookie) {
        String url = erpnextProperties.getUrl() + endpoint;
        HttpHeaders headers = createHeaders(sessionCookie);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
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
            throw new ErpApiException("Erreur lors de l'envoi POST vers l'API ERPNext", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public JsonNode postJsonDataToApiWithMessage(String endpoint, String jsonBody, String sessionCookie) {
        String url = erpnextProperties.getUrl() + endpoint;
        HttpHeaders headers = createHeaders(sessionCookie);
        HttpEntity<String> request = new HttpEntity<>(jsonBody, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                JsonNode root = objectMapper.readTree(response.getBody());
                JsonNode messageNode = root.get("message");
                if (messageNode == null || messageNode.isNull()) {
                    throw new ErpApiException("Le champ 'message' est absent dans la réponse de l’API.", HttpStatus.NO_CONTENT.value());
                }
                return messageNode;
            }
            else {
                throw new ErpApiException("Erreur HTTP reçue : " + response.getStatusCode().value(), response.getStatusCode().value());
            }
        }
        catch (ErpApiException e) {
            throw e;
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de l'envoi POST (avec message) vers l'API ERPNext", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

}
