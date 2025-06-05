package com.lucio.erp_new_app_3.controllers;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.salary.SalaryReportResponse;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlip;
import com.lucio.erp_new_app_3.services.pdf.PdfGeneratorService;
import com.lucio.erp_new_app_3.services.salary.SalaryRegisterService;
import com.lucio.erp_new_app_3.services.salary.SalarySlipService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/salary")
public class SalarySlipController {
    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private PdfGeneratorService pdfGeneratorService;

    @Autowired
    private SalaryRegisterService salaryRegisterService;

    @GetMapping("/fiche-paie/{abbrev}/{empName}/{numero}")
    public ModelAndView fichePaie(@PathVariable String abbrev, @PathVariable String empName, @PathVariable String numero, HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        String id = abbrev+"/"+empName+"/"+numero;
        SalarySlip fiche = salarySlipService.getSalarySlip(id, sessionCookie);

        modelAndView.addObject("fiche", fiche);
        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Fiche de paie", "pages/salary/fiche-paie");

        return modelAndView;
    }

    @GetMapping("/fiche-paie/{abbrev}/{empName}/{numero}/pdf")
    public ResponseEntity<byte[]> exportFichePaiePdf(
            @PathVariable String abbrev,
            @PathVariable String empName,
            @PathVariable String numero,
            HttpSession session) {

        String sessionCookie = (String) session.getAttribute("sid");
        if (sessionCookie == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String id = abbrev + "/" + empName + "/" + numero;
        SalarySlip fiche = salarySlipService.getSalarySlip(id, sessionCookie);

        Map<String, Object> data = new HashMap<>();
        data.put("fiche", fiche);

        byte[] pdfBytes = pdfGeneratorService.generatePdfFromThymeleaf("pages/export/fiche-paie-pdf", data);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Fiche_Paie_" + fiche.getEmployeeName() + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    @GetMapping("/summary")
    public ModelAndView getSalaryRegister(@RequestParam(required = false) Integer month,
                                        @RequestParam(required = false) Integer year,
                                        HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        if (month == null) month = LocalDate.now().getMonthValue();
        if (year == null) year = LocalDate.now().getYear();

        SalaryReportResponse reportResponse = salaryRegisterService.getSalaryRegisterReport(sessionCookie);

        modelAndView.addObject("salaryRegisters", reportResponse.getSalaryRegisters());
        modelAndView.addObject("totals", reportResponse.getTotals());
        modelAndView.addObject("months", List.of(1,2,3,4,5,6,7,8,9,10,11,12));
        modelAndView.addObject("years", List.of(2023, 2024, 2025));
        modelAndView.addObject("selectedMonth", month);
        modelAndView.addObject("selectedYear", year);

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Rapport Salary Register", "pages/salary/register");

        return modelAndView;
    }

    // @GetMapping("/summary")
    // public ModelAndView getSalarySummary(@RequestParam(required = false) Integer month,
    //                                 @RequestParam(required = false) Integer year,
    //                                 HttpSession session) {

    //     String sessionCookie = (String) session.getAttribute("sid");
    //     ModelAndView modelAndView = new ModelAndView("layout/modele");

    //     if (sessionCookie == null) {
    //         modelAndView.setViewName("redirect:/");
    //         return modelAndView;
    //     }

    //     if (month == null) month = LocalDate.now().getMonthValue();
    //     if (year == null) year = LocalDate.now().getYear();

    //     List<SalarySlip> salarySlips = salarySlipService.getSalarySlipsByMonthYear(month, year, sessionCookie);
    //     SalarySummaryTotals totals = calculateTotals(salarySlips);

    //     modelAndView.addObject("salarySlips", salarySlips);
    //     modelAndView.addObject("totals", totals);
    //     modelAndView.addObject("months", List.of(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12));
    //     modelAndView.addObject("years", List.of(2023, 2024, 2025));
    //     modelAndView.addObject("selectedMonth", month);
    //     modelAndView.addObject("selectedYear", year);

    //     EnvoyeInformation.afficherName(session, modelAndView);
    //     EnvoyeInformation.setInfo(modelAndView, "Fiche de paie", "pages/salary/summary");

    //     return modelAndView;
    // }

    // private SalarySummaryTotals calculateTotals(List<SalarySlip> salarySlips) {
    //     SalarySummaryTotals totals = new SalarySummaryTotals();

    //     salarySlips.forEach(slip -> {
    //         totals.setTotalGrossPay(totals.getTotalGrossPay() + slip.getGrossPay());
    //         totals.setTotalDeductions(totals.getTotalDeductions() + slip.getTotalDeduction());
    //         totals.setTotalNetPay(totals.getTotalNetPay() + slip.getNetPay());
    //         totals.setTotalCtc(totals.getTotalCtc() + slip.getCtc());
    //     });

    //     return totals;
    // }

}
