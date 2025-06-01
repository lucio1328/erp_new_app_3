package com.lucio.erp_new_app_3.services.poste;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.poste.Designation;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

@Service
public class DesignationService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public DesignationService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public List<Designation> getAllPostes(String sessionCookie) {
        String endpoint = "/api/resource/Designation?fields=[\"*\"]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(Designation.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des designations", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }
}
