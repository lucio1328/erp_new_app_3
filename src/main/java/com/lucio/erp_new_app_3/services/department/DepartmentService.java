package com.lucio.erp_new_app_3.services.department;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.department.Department;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

@Service
public class DepartmentService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public DepartmentService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public List<Department> getAllDepartments(String sessionCookie) {
        String endpoint = "/api/resource/Department?fields=[\"*\"]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(Department.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des departements", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }
}
