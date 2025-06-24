package com.lucio.erp_new_app_3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.salary.assignment.StructureDetail;
import com.lucio.erp_new_app_3.services.data.DataService;
import com.lucio.erp_new_app_3.services.employee.EmployeeService;
import com.lucio.erp_new_app_3.services.salary.SalaryAssignmentService;
import com.lucio.erp_new_app_3.services.salary.SalaryStructureService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/structures")
public class SalaryAssignmentController {
    @Autowired
    private SalaryAssignmentService salaryAssignmentService;

    @Autowired
    private DataService dataService;

    @Autowired
    private SalaryStructureService salaryStructureService;

    @Autowired
    private EmployeeService employeeService;

    @GetMapping("/assignment")
    public ModelAndView showAssignmentForm(HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Ajouter Structure Salariale", "pages/attribution/insert");

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

    @PostMapping("/assign")
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
            List<StructureDetail> structureDetails = mapper.readValue(
                structureDetailsJson,
                new TypeReference<List<StructureDetail>>() {}
            );
            salaryAssignmentService.assignToSalaryStructures(session, salaryStructure, company, fromDate, currency, structureDetails);
            return "{\"status\":\"success\", \"message\":\"Assignation réussie\"}";
        }
        catch (Exception e) {
            return "{\"status\":\"error\", \"message\":\"" + e.getMessage().replace("\"", "\\\"") + "\"}";
        }
    }
}
