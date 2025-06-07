package com.lucio.erp_new_app_3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.company.Company;
import com.lucio.erp_new_app_3.dtos.devise.Currency;
import com.lucio.erp_new_app_3.dtos.salary.component.SalaryComponent;
import com.lucio.erp_new_app_3.services.company.CompanyService;
import com.lucio.erp_new_app_3.services.devise.CurrencyService;
import com.lucio.erp_new_app_3.services.salary.SalaryComponentService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("grille")
public class GrilleController {

    @Autowired
    private CompanyService companyService;

    @Autowired
    private CurrencyService currencyService;

    @Autowired
    private SalaryComponentService salaryComponentService;

    @GetMapping("/insert")
    public ModelAndView insertGrille(HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        List<Company> companies = companyService.getAllCompany(sessionCookie);
        List<Currency> currencies = currencyService.getAllCurrency(sessionCookie);
        List<SalaryComponent> earnings = salaryComponentService.getByType(sessionCookie, "earning");
        List<SalaryComponent> deductions = salaryComponentService.getByType(sessionCookie, "deduction");

        modelAndView.addObject("companies", companies);
        modelAndView.addObject("currencies", currencies);
        modelAndView.addObject("earningComponents", earnings);
        modelAndView.addObject("deductionComponents", deductions);
        modelAndView.addObject("payFrequencies", List.of("Monthly","Weekly","Biweekly","Daily"));

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Insertion Grille", "pages/grille/insert");

        return modelAndView;
    }
}
