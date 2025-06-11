package com.lucio.erp_new_app_3.services.alea;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucio.erp_new_app_3.dtos.alea.ModifCache;
import com.lucio.erp_new_app_3.dtos.alea.ModifSalaire;
import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.dtos.salary.details_salary.SalaryDeduction;
import com.lucio.erp_new_app_3.dtos.salary.details_salary.SalaryEarning;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlip;
import com.lucio.erp_new_app_3.services.employee.EmployeeService;
import com.lucio.erp_new_app_3.services.salary.SalarySlipService;

@Service
public class ModificationService {

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ModifCache modifCache;

    public void annulerSalarySlips() {
        for(SalarySlip salarySlip : modifCache.getSalarySlips()) {

        }
    }

    public List<Employee> getEmpConcerne(String session, ModifSalaire modifSalaire) {
        List<Employee> employees = new ArrayList<>();
        List<SalarySlip> salarySlips = salarySlipService.getSalarySlips(session);
        List<SalarySlip> sals = new ArrayList<>();

        for (SalarySlip salarySlip : salarySlips) {
            salarySlip = salarySlipService.getSalarySlip(salarySlip.getName(), session);
            sals.add(salarySlip);
        }

        salarySlips = filtrer(sals, modifSalaire);
        modifCache.setSalarySlips(salarySlips);

        employeeService.addEmployes(employeeService.getAllEmployees(session));

        for (SalarySlip salarySlip2: salarySlips) {
            employees.add(employeeService.getEmployee(salarySlip2.getEmployee()));
        }
        modifCache.setEmployees(employees);

        return employees;
    }

    public List<SalarySlip> filtrer(List<SalarySlip> salarySlips, ModifSalaire modifSalaire) {
        List<SalarySlip> salarySlips2 = new ArrayList<>();

        for (SalarySlip salarySlip : salarySlips) {
            for (SalaryEarning salaryEarning : salarySlip.getEarnings()) {
                if (condition(salaryEarning.getAmount(), modifSalaire)) {
                    salarySlips2.add(salarySlip);
                }
            }

            for (SalaryDeduction salaryDeduction : salarySlip.getDeductions()) {
                if (condition(salaryDeduction.getAmount(), modifSalaire)) {
                    salarySlips2.add(salarySlip);
                }
            }
        }

        return salarySlips2;
    }

    public boolean condition(Double valAComparer, ModifSalaire modifSalaire) {
        boolean estVrai = false;

        if (modifSalaire.getCondition().equals("supp")) {
            if (valAComparer > Double.parseDouble(modifSalaire.getValeur())) {
                estVrai = true;
            }
        }
        else {
            if (valAComparer < Double.parseDouble(modifSalaire.getValeur())) {
                estVrai = true;
            }
        }

        return estVrai;
    }
}
