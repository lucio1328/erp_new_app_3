package com.lucio.erp_new_app_3.services.salary;

import java.util.List;

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

    public List<SalaryComponent> getByType(String sessionCookie, String type) {
        try {
            String filters = String.format("[[\"Salary Component\",\"type\",\"=\",\"%s\"]]", type);
            String encodedFilters = java.net.URLEncoder.encode(filters, java.nio.charset.StandardCharsets.UTF_8);

            String endpoint = "/api/resource/Salary Component"
                            + "?fields=[\"*\"]"
                            + "&filters=" + encodedFilters;

            JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);

            return objectMapper.readerForListOf(SalaryComponent.class).readValue(data);
        } catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des salary components", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }
}
