package com.lucio.erp_new_app_3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.services.csv.CsvEmployeImporter;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/import")
public class ImportController {
    @Autowired
    private CsvEmployeImporter csvEmployeImporter;

    @GetMapping
    public ModelAndView formulaire(HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        if (sessionCookie == null) {
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Import csv", "pages/import/form");

        return modelAndView;
    }

    @PostMapping("/upload")
    public ModelAndView handleImport(@RequestParam("fichierEmploye") MultipartFile fichierEmploye,
                                    HttpSession session) {
        String sessionCookie = (String) session.getAttribute("sid");
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        CsvEmployeImporter.ResultatImport resultat = csvEmployeImporter.traiterCsvEmployes(fichierEmploye, sessionCookie);

        modelAndView.addObject("message", resultat.message());
        modelAndView.addObject("erreurs", resultat.erreurs());
        modelAndView.addObject("lignesErronees", resultat.lignesErronees());

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Résultat import", "pages/import/form");
        return modelAndView;
    }

}
