package com.lucio.erp_new_app_3.services.salary;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.salary.component.SalaryComponent;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

@Service
public class SalaryComponentService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public SalaryComponentService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public SalaryComponent getByName(String name, String sessionCookie) {
        String endpoint = "/api/resource/Salary Component/" + name;

        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);

        try {
            return objectMapper.treeToValue(data, SalaryComponent.class);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing du SalaryComponent " + name, HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }
}
