package com.lucio.erp_new_app_3.services.alea;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    public void recreerSalarySlips(String session, ModifSalaire modifSalaire) {
        annulerSalarySlips(session);
        System.out.println("Salary Slip Annule!!!!!!!!!!!!!!!");

        for (SalarySlip oldSlip : modifCache.getSalarySlips()) {
            SalarySlip newSlip = new SalarySlip();
            newSlip.setEmployee(oldSlip.getEmployee());
            newSlip.setStartDate(oldSlip.getStartDate());
            newSlip.setEndDate(oldSlip.getEndDate());
            newSlip.setCompany(oldSlip.getCompany());
            newSlip.setCurrency(oldSlip.getCurrency());
            newSlip.setSalaryStructure(oldSlip.getSalaryStructure());

            List<SalaryEarning> newEarnings = new ArrayList<>();
            for (SalaryEarning earning : oldSlip.getEarnings()) {
                SalaryEarning newEarning = new SalaryEarning();
                newEarning.setSalaryComponent(earning.getSalaryComponent());

                if (earning.getSalaryComponent().equals(modifSalaire.getComposante())) {
                    double oldAmount = earning.getAmount();
                    double percentage = Double.parseDouble(modifSalaire.getValPourcentage());

                    double newAmount = modifSalaire.getPourcentage().equals("plus")
                            ? oldAmount * (1 + percentage / 100)
                            : oldAmount * (1 - percentage / 100);

                    newEarning.setAmount(newAmount);
                }
                else {
                    newEarning.setAmount(earning.getAmount());
                }

                newEarnings.add(newEarning);
            }

            newSlip.setEarnings(newEarnings);
            newSlip.setDeductions(oldSlip.getDeductions());

            salarySlipService.createSalarySlip(newSlip, session);
        }
    }

    public void annulerSalarySlips(String sessionCookie) {
        for(SalarySlip salarySlip : modifCache.getSalarySlips()) {
            salarySlipService.cancelSalarySlip(salarySlip.getName(), sessionCookie);
        }
    }

    public List<Employee> getEmpConcerne(String session, ModifSalaire modifSalaire) {
        List<SalarySlip> salarySlips = salarySlipService.getSalarySlips(session);
        List<SalarySlip> sals = new ArrayList<>();

        for (SalarySlip salarySlip : salarySlips) {
            salarySlip = salarySlipService.getSalarySlip(salarySlip.getName(), session);
            sals.add(salarySlip);
        }

        salarySlips = filtrer(sals, modifSalaire);
        modifCache.setSalarySlips(salarySlips);

        employeeService.addEmployes(employeeService.getAllEmployees(session));

        Set<Employee> employeesSet = new HashSet<>();
        for (SalarySlip salarySlip2 : salarySlips) {
            Employee emp = employeeService.getEmployee(salarySlip2.getEmployee());
            employeesSet.add(emp);
        }

        List<Employee> employees = new ArrayList<>(employeesSet);
        modifCache.setEmployees(employees);

        return employees;
    }

    public List<SalarySlip> filtrer(List<SalarySlip> salarySlips, ModifSalaire modifSalaire) {
        List<SalarySlip> salarySlips2 = new ArrayList<>();

        for (SalarySlip salarySlip : salarySlips) {
            boolean trouve = false;

            for (SalaryEarning salaryEarning : salarySlip.getEarnings()) {
                if (condition(salaryEarning.getSalaryComponent(), salaryEarning.getAmount(), modifSalaire)) {
                    salarySlips2.add(salarySlip);
                    trouve = true;
                    break;
                }
            }

            if (trouve) continue;

            for (SalaryDeduction salaryDeduction : salarySlip.getDeductions()) {
                if (condition(salaryDeduction.getSalaryComponent(), salaryDeduction.getAmount(), modifSalaire)) {
                    salarySlips2.add(salarySlip);
                    break;
                }
            }
        }

        return salarySlips2;
    }

    public boolean condition(String composante, Double valAComparer, ModifSalaire modifSalaire) {
        boolean estVrai = false;

        if (modifSalaire.getCondition().equals("supp")) {
            if (composante.equals(modifSalaire.getComposante()) && valAComparer > Double.parseDouble(modifSalaire.getValeur())) {
                estVrai = true;
            }
        }
        else {
            if (composante.equals(modifSalaire.getComposante()) && valAComparer < Double.parseDouble(modifSalaire.getValeur())) {
                estVrai = true;
            }
        }

        return estVrai;
    }
}
