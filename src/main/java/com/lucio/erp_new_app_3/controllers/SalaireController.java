package com.lucio.erp_new_app_3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.lucio.erp_new_app_3.dtos.alea.GenereSalaire;
import com.lucio.erp_new_app_3.dtos.alea.ModifSalaire;
import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.services.alea.GenererService;
import com.lucio.erp_new_app_3.services.alea.ModificationService;
import com.lucio.erp_new_app_3.services.data.DataService;
import com.lucio.erp_new_app_3.services.employee.EmployeeService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/salaire")
public class SalaireController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DataService dataService;

    @Autowired
    private ModificationService modificationService;

    @Autowired
    private GenererService genererService;


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
        try {
            genererService.genererSalaire(session, genereSalaire);

            redirectAttributes.addFlashAttribute("success", "Salaire generé avec succes");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return new ModelAndView("redirect:/salaire/generer");
    }


    @GetMapping("/modification")
    public ModelAndView modification(HttpSession session) {
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
        try {
            List<Employee> employees = modificationService.getEmpConcerne(sessionCookie, modifSalaire);

            for (Employee employee : employees) {
                System.out.println("Employe Details: "+ employee);
            }

            // model.addAttribute("employes", employees);
            redirectAttributes.addFlashAttribute("success", "Liste Employes Concernes: " + employees + "\n");
        }
        catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur : " + e.getMessage());
        }

        return new ModelAndView("redirect:/salaire/modification");
    }
}
