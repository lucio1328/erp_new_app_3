package com.lucio.erp_new_app_3.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlip;
import com.lucio.erp_new_app_3.services.employee.EmployeeService;
import com.lucio.erp_new_app_3.services.employee.GenreService;
import com.lucio.erp_new_app_3.services.poste.DesignationService;
import com.lucio.erp_new_app_3.services.salary.SalarySlipService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;
import com.lucio.erp_new_app_3.utils.PaginationUtils;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/employes")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private DesignationService designationService;


    @GetMapping
    public ModelAndView listeEmployes(HttpSession session,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(required = false) String employeeName,
                                    @RequestParam(required = false) String gender,
                                    @RequestParam(required = false) String designation,
                                    @RequestParam(required = false) String department,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        List<Employee> allEmployees = employeeService.getAllEmployees(sessionCookie);

        List<Employee> filteredEmployees = employeeService.filtreEmployees(allEmployees, employeeName, gender, designation, department, startDate, endDate);

        List<Employee> paginatedEmployees = PaginationUtils.paginate(filteredEmployees, page, size);
        int totalPages = PaginationUtils.getTotalPages(filteredEmployees.size(), size);

        modelAndView.addObject("employees", paginatedEmployees);
        modelAndView.addObject("currentPage", page);
        modelAndView.addObject("totalPages", totalPages);

        modelAndView.addObject("employeeName", employeeName);
        modelAndView.addObject("gender", gender);
        modelAndView.addObject("designation", designation);
        modelAndView.addObject("department", department);
        modelAndView.addObject("startDate", startDate);
        modelAndView.addObject("endDate", endDate);

        modelAndView.addObject("departments", employeeService.getDepartments(allEmployees));
        modelAndView.addObject("designations", designationService.getAllPostes(sessionCookie));
        modelAndView.addObject("genres", genreService.getAllGenres(sessionCookie));

        employeeService.addEmployes(paginatedEmployees);

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Liste des employés", "pages/employee/liste");

        return modelAndView;
    }


    @GetMapping("/fiche/{employeeId}")
    public ModelAndView ficheEmploye(HttpSession session,
                                @PathVariable String employeeId,
                                @RequestParam(required = false, defaultValue = "Tous") String month,
                                @RequestParam(required = false, defaultValue = "Toutes") String year) {

        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        Employee employee = employeeService.getEmployee(employeeId);

        System.out.println("Employe: "+ employee);

        List<SalarySlip> salarySlips = salarySlipService.getSalarySlipsByEmployeeWithFilters(employeeId, month, year, sessionCookie);

        modelAndView.addObject("employee", employee);
        modelAndView.addObject("salarySlips", salarySlips);

        modelAndView.addObject("months", List.of("Tous", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"));
        modelAndView.addObject("years", List.of("Toutes", "2023", "2024", "2025"));
        modelAndView.addObject("selectedMonth", month);
        modelAndView.addObject("selectedYear", year);

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Fiche employé", "pages/employee/fiche");

        return modelAndView;
    }
}

