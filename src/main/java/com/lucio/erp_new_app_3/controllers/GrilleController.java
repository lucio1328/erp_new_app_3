package com.lucio.erp_new_app_3.controllers;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lucio.erp_new_app_3.dtos.salary.SalaryStructure;
import com.lucio.erp_new_app_3.services.data.DataService;
import com.lucio.erp_new_app_3.services.salary.SalaryStructureService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/grille")
public class GrilleController {

    @Autowired
    private DataService dataService;

    @Autowired
    private SalaryStructureService salaryStructureService;

    @GetMapping("/insert")
    public ModelAndView insertGrille(HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        // List<Currency> currencies = currencyService.getAllCurrency(sessionCookie);
        // modelAndView.addObject("currencies", currencies);
        modelAndView.addObject("salaryGridDTO", new SalaryStructure());
        modelAndView.addObject("companies", dataService.getAllData(session, "Company", null));
        modelAndView.addObject("earnings", dataService.getAllData(session, "Salary Component", "earning"));
        modelAndView.addObject("deductions", dataService.getAllData(session, "Salary Component", "deduction"));

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Insertion Grille", "pages/grille/insert");

        return modelAndView;
    }

    @SuppressWarnings("null")
    @PostMapping("/insert")
    public ModelAndView createSalaryGrid(HttpSession session,
                                        @ModelAttribute SalaryStructure salaryGridDTO,
                                        RedirectAttributes redirectAttributes) {
        try {
            ResponseEntity<Map<String, Object>> response = salaryStructureService.createSalaryGrid(session, salaryGridDTO);

            if (response.getStatusCode().is2xxSuccessful()) {
                salaryStructureService.submitSalaryGrid(session, salaryGridDTO.getName());
                redirectAttributes.addFlashAttribute("success", "Grille de salaire créée avec succès.");
            }
            else {
                String error = response.getBody() != null ? (String) response.getBody().get("error") : "Erreur inconnue";
                redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + error);
            }
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return new ModelAndView("redirect:/grille/insert");
    }
}
