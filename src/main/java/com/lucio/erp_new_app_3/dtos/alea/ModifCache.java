package com.lucio.erp_new_app_3.dtos.alea;

import java.util.List;

import org.springframework.stereotype.Component;

import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlip;

import lombok.Data;

@Data
@Component
public class ModifCache {
    List<Employee> employees;
    List<SalarySlip> salarySlips;

    public void addEmployes(Employee employee) {
        this.employees.add(employee);
    }

    public void addSalarySlips(SalarySlip salarySlip) {
        this.salarySlips.add(salarySlip);
    }
}
