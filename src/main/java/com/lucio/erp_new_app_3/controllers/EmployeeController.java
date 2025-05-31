package com.lucio.erp_new_app_3.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.services.employee.EmployeeService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;
import com.lucio.erp_new_app_3.utils.PaginationUtils;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/employes")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;


    @GetMapping
    public ModelAndView listeEmployes(HttpSession session,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        List<Employee> allEmployees = employeeService.getAllEmployees(sessionCookie);
        List<Employee> paginatedEmployees = PaginationUtils.paginate(allEmployees, page, size);
        int totalPages = PaginationUtils.getTotalPages(allEmployees.size(), size);

        modelAndView.addObject("employees", paginatedEmployees);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", totalPages);

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Liste des employés", "pages/employee/liste");

        return modelAndView;
    }
}

