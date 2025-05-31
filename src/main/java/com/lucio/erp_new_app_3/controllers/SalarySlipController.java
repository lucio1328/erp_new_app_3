package com.lucio.erp_new_app_3.controllers;

import java.util.HashMap;
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
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.salary.SalarySlip;
import com.lucio.erp_new_app_3.services.pdf.PdfGeneratorService;
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
        headers.setContentDispositionFormData("attachment", "fiche-paie-" + empName + ".pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // @GetMapping("/fiche-paie/{abbrev}/{empName}/{numero}/pdf")
    // public ResponseEntity<byte[]> generatePdf(@PathVariable String abbrev, @PathVariable String empName, @PathVariable String numero, HttpSession session) {
    //     String sessionCookie = (String) session.getAttribute("sid");
    //     if (sessionCookie == null) {
    //         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    //     }

    //     String name = abbrev+"/"+empName+"/"+numero;
    //     SalarySlip fiche = salarySlipService.getSalarySlip(name, sessionCookie);
    //     Map<String, Object> data = new HashMap<>();
    //     data.put("fiche", fiche);

    //     byte[] pdfBytes = pdfGeneratorService.generatePdfFromHtml("pages/salary/fiche-paie", data);

    //     HttpHeaders headers = new HttpHeaders();
    //     headers.setContentType(MediaType.APPLICATION_PDF);
    //     headers.setContentDispositionFormData("filename", "fiche-paie-" + name + ".pdf");

    //     return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    // }

}
