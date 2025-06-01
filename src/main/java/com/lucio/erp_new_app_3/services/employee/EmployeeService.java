package com.lucio.erp_new_app_3.services.employee;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.dtos.employee.EmployeeCache;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    @Autowired
    private EmployeeCache employeeCache;

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
            throw new ErpApiException("Erreur de parsing des employés", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public void addEmployes(List<Employee> employees) {
        if (employees.size() > 0) {
            for (Employee employee : employees) {
                employeeCache.addEmployee(employee);
            }
        }
        return;
    }

    public Employee getEmployee(String id) {
        if (id != null) {
            return employeeCache.getEmployeeById(id);
        }
        return null;
    }

    public List<String> getDepartments(List<Employee> employees) {
        return employees.stream()
                .map(Employee::getDepartment)
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getDesignations(List<Employee> employees) {
        return employees.stream()
                .map(Employee::getDesignation)
                .distinct()
                .sorted()
                .toList();
    }

    public List<String> getGenres(List<Employee> employees) {
        return employees.stream()
                .map(Employee::getGender)
                .distinct()
                .sorted()
                .toList();
    }

    public List<Employee> filtreEmployees(List<Employee> employees, String employeeName,
                                        String gender,
                                        String designation,
                                        String department,
                                        LocalDate startDate,
                                        LocalDate endDate) {

        return employees.stream()
                        .filter(emp -> employeeName == null || emp.getEmployeeName().toLowerCase().contains(employeeName.toLowerCase()))
                        .filter(emp -> gender == null || gender.isEmpty() || gender.equalsIgnoreCase(emp.getGender()))
                        .filter(emp -> department == null || department.isEmpty() || department.equalsIgnoreCase(emp.getDepartment()))
                        .filter(emp -> designation == null || designation.isEmpty() || designation.equalsIgnoreCase(emp.getDesignation()))
                        .filter(emp -> {
                            LocalDate joiningDate = emp.getDateOfJoining();
                            return (startDate == null || !joiningDate.isBefore(startDate)) &&
                                (endDate == null || !joiningDate.isAfter(endDate));
                        })
                        .toList();
    }
}

