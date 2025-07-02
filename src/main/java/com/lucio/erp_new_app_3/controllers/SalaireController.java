package com.lucio.erp_new_app_3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.alea.GenereSalaire;
import com.lucio.erp_new_app_3.dtos.alea.ModifCache;
import com.lucio.erp_new_app_3.dtos.alea.ModifSalaire;
import com.lucio.erp_new_app_3.dtos.salary.assignment.StructureDetail;
import com.lucio.erp_new_app_3.services.alea.GenererService;
import com.lucio.erp_new_app_3.services.alea.ModificationService;
import com.lucio.erp_new_app_3.services.data.DataService;
import com.lucio.erp_new_app_3.services.employee.EmployeeService;
import com.lucio.erp_new_app_3.services.salary.SalaryAssignmentService;
import com.lucio.erp_new_app_3.services.salary.SalaryStructureService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/salaire")
public class SalaireController {

    @Autowired
    private SalaryAssignmentService salaryAssignmentService;

    @Autowired
    private DataService dataService;

    @Autowired
    private SalaryStructureService salaryStructureService;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private ModificationService modificationService;

    @Autowired
    private GenererService genererService;

    @GetMapping("/generer-direct")
    public ModelAndView showAssignmentForm(HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Ajouter Salaire", "pages/salary/insert-direct");

        try {
            modelAndView.addObject("structureDetails", new StructureDetail());
            modelAndView.addObject("companies", dataService.getAllData(session, "Company", null).getData());
            modelAndView.addObject("structures", salaryStructureService.getAllStructure(sessionCookie));
            modelAndView.addObject("employees", employeeService.getAllEmployees(null));
        }
        catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }

    @PostMapping("/generer-direct")
    @ResponseBody
    public String assignSalaryStructures(
            HttpSession session,
            @RequestParam("salaryStructure") String salaryStructure,
            @RequestParam("company") String company,
            @RequestParam("fromDate") String fromDate,
            @RequestParam("currency") String currency,
            @RequestParam("structureDetails") String structureDetailsJson) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            StructureDetail structureDetails = mapper.readValue(
                structureDetailsJson,
                new TypeReference<StructureDetail>() {}
            );
            // salaryAssignmentService.assignToSalaryStructures(session, salaryStructure, company, fromDate, currency, structureDetails);
            return "{\"status\":\"success\", \"message\":\"Assignation réussie\"}";
        }
        catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }


    @GetMapping("/generer")
    public ModelAndView generer(HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        modelAndView.addObject("salaireData", new GenereSalaire());
        modelAndView.addObject("employes", employeeService.getAllEmployees(sessionCookie));

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Generer Salaire", "pages/alea/generer");

        return modelAndView;
    }

    @PostMapping("/generer")
    public ModelAndView genererSalaire(HttpSession session,
                                        @ModelAttribute GenereSalaire genereSalaire,
                                        RedirectAttributes redirectAttributes) {
        String sessionCookie = (String) session.getAttribute("sid");
        try {
            if (sessionCookie == null) {
                return new ModelAndView("redirect:/");
            }
            genererService.genererSalaire(sessionCookie, genereSalaire);

            redirectAttributes.addFlashAttribute("success", "Salaire generé avec succes");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return new ModelAndView("redirect:/salaire/generer");
    }


    @GetMapping("/modification")
    public ModelAndView modification(HttpSession session, Model model) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        modelAndView.addObject("modifSalaire", new ModifSalaire());
        modelAndView.addObject("composantes", dataService.getAllData(session, "Salary Component", null));

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Modification Salaire", "pages/alea/modif");

        return modelAndView;
    }

    @PostMapping("/modification")
    public ModelAndView modificationSalaire(HttpSession session,
                                        @ModelAttribute ModifSalaire modifSalaire,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");
        try {
            if (sessionCookie == null) {
                modelAndView.setViewName("redirect:/");
                return modelAndView;
            }

            // List<Employee> employees = modificationService.getEmpConcerne(sessionCookie, modifSalaire);
            modificationService.recreerSalarySlips(sessionCookie, modifSalaire);

            modelAndView.addObject("modifSalaire", new ModifSalaire());
            modelAndView.addObject("composantes", dataService.getAllData(session, "Salary Component", null));

            EnvoyeInformation.afficherName(session, modelAndView);
            EnvoyeInformation.setInfo(modelAndView, "Modification Salaire", "pages/alea/modif");

            // modelAndView.addObject("employesConcernes", employees);
            modelAndView.addObject("success", "Modification reussie!!");
        }
        catch (Exception e) {
            modelAndView.addObject("error", "Erreur : " + e.getMessage());
        }

        return modelAndView;
    }

    @GetMapping("/recherche")
    public ModelAndView recherche(HttpSession session, Model model) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        modelAndView.addObject("modifSalaire", new ModifSalaire());
        modelAndView.addObject("composantes", dataService.getAllData(session, "Salary Component", null));

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Recherche Salaire", "pages/alea/recherche");

        return modelAndView;
    }

    @PostMapping("/recherche")
    public ModelAndView rechercheSalaire(HttpSession session,
                                        @ModelAttribute ModifSalaire modifSalaire,
                                        RedirectAttributes redirectAttributes,
                                        Model model) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");
        try {
            if (sessionCookie == null) {
                modelAndView.setViewName("redirect:/");
                return modelAndView;
            }

            ModifCache modifCache = modificationService.getEmpConcerne(sessionCookie, modifSalaire);

            modelAndView.addObject("modifSalaire", modifSalaire);
            modelAndView.addObject("composantes", dataService.getAllData(session, "Salary Component", null));

            EnvoyeInformation.afficherName(session, modelAndView);
            EnvoyeInformation.setInfo(modelAndView, "Modification Salaire", "pages/alea/recherche");

            modelAndView.addObject("employesConcernes", modifCache.getEmployees());
            modelAndView.addObject("salarySlips", modifCache.getSalarySlips());
            modelAndView.addObject("success", "Recherche reussie!!");
        }
        catch (Exception e) {
            modelAndView.addObject("error", "Erreur : " + e.getMessage());
        }

        return modelAndView;
    }
}
