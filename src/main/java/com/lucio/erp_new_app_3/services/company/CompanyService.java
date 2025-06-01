package com.lucio.erp_new_app_3.services.company;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.company.Company;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

@Service
public class CompanyService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public CompanyService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public List<Company> getAllCompany(String sessionCookie) {
        String endpoint = "/api/resource/Company?fields=[\"*\"]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(Company.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des company", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public Company getByName(String name, String sessionCookie) {
        String endpoint = "/api/resource/Company/" + name;

        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);

        try {
            return objectMapper.treeToValue(data, Company.class);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing du company " + name, HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public Company create(Company company, String sessionCookie) {
        String endpoint = "/api/resource/Gender";
        try {
            String jsonBody = objectMapper.writeValueAsString(company);
            JsonNode response = preparationApi.postJsonDataToApi(endpoint, jsonBody, sessionCookie);

            if (response == null || response.isNull()) {
                throw new ErpApiException("Erreur lors de la création du company", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            return objectMapper.treeToValue(response, Company.class);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la création du company", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }
}
