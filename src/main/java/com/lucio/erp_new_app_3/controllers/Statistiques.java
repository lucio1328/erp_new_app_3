package com.lucio.erp_new_app_3.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.statistiques.StatLigne;
import com.lucio.erp_new_app_3.services.statistiques.StatistiquesService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/statistiques")
public class Statistiques {

    @Autowired
    private StatistiquesService statistiquesService;

    @GetMapping
    public ModelAndView getStatistiques(HttpSession session, @RequestParam(defaultValue = "2025") int annee) {
        ModelAndView modelAndView = new ModelAndView("layout/modele");
        List<String> elements = Arrays.asList("Salaire de base", "Prime", "Indemnité");

        List<StatLigne> statistiques = statistiquesService.calculerStatistiques(annee);

        modelAndView.addObject("annee", annee);
        modelAndView.addObject("elements", elements);
        modelAndView.addObject("statistiques", statistiques);

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Statistiques", "pages/statistiques/statistiques");

        return modelAndView;
    }

}
