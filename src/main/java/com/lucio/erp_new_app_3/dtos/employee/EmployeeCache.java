package com.lucio.erp_new_app_3.dtos.employee;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class EmployeeCache {
    List<Employee> listEmployees;
    private Map<String, Employee> employeeMap;

    public EmployeeCache() {
        this.listEmployees = new ArrayList<>();
        this.employeeMap = new HashMap<>();
    }

    public Employee getEmployeeById(String employeeId) {
        return employeeMap.get(employeeId);
    }

    public void addEmployee(Employee employee) {
        employeeMap.put(employee.getName(), employee);
        listEmployees.removeIf(e -> e.getName().equals(employee.getName()));
        listEmployees.add(employee);
    }
}
