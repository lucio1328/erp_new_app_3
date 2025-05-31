package com.lucio.erp_new_app_3.services.employee;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.utils.PreparationApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private ObjectMapper objectMapper;
    private PreparationApi preparationApi;

    public EmployeeService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public List<Employee> getAllEmployees(String sessionCookie) {
        String endpoint = "/api/resource/Employee?fields=[\"*\"]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(Employee.class).readValue(data);
        }
        catch (Exception e) {
            throw new RuntimeException("Erreur de parsing des employés", e);
        }
    }
}

