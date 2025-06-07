package com.lucio.erp_new_app_3.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.data.DataDto;
import com.lucio.erp_new_app_3.dtos.salary.SalaryTotalsResponse;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlip;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlipModele;
import com.lucio.erp_new_app_3.services.data.DataService;
import com.lucio.erp_new_app_3.services.statistiques.StatistiquesService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/statistiques")
public class StatistiquesController {

    @Autowired
    private StatistiquesService statistiquesService;

    @Autowired
    private DataService dataService;

    @GetMapping("/stat")
    public ModelAndView groupedSalarySummary(HttpSession session,
                                        @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") String year) {
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Statistiques", "pages/statistiques/statistiques");

        modelAndView.addObject("selectedYear", year);

        try {
            List<DataDto> salaryComponents = dataService.getAllData(session, "Salary Component").getData();
            Map<String, SalarySlip> groupedSalarySlips = statistiquesService.getSalarySlipsGroupedByMonth(session, year, salaryComponents);
            List<SalarySlip> salarySlipDtos=new ArrayList<>();
            for (Map.Entry<String, SalarySlip> entry : groupedSalarySlips.entrySet()) {
                SalarySlip salarySlipDto = entry.getValue();
                salarySlipDtos.add(salarySlipDto);

            }
            modelAndView.addObject("salaryComponents", salaryComponents);
            modelAndView.addObject("groupedSalarySlips", groupedSalarySlips);
            modelAndView.addObject("totalSalarySlip", new SalaryTotalsResponse(salarySlipDtos,salaryComponents));

            modelAndView.addObject("componentNames", salaryComponents.stream().map(DataDto::getName).collect(Collectors.toList()));

            List<String> moisList = groupedSalarySlips.values().stream()
                                        .map(slip -> slip.getMois())
                                        .collect(Collectors.toList());

            modelAndView.addObject("moisList", moisList);

            List<SalarySlipModele> salarySlipDTOs = groupedSalarySlips.values().stream()
                    .map(slip -> new SalarySlipModele(
                        slip.getMois(),
                        slip.getGrossPay(),
                        slip.getTotalDeduction(),
                        slip.getNetPay(),
                        slip.getComponentsDef()
                    )).collect(Collectors.toList());

            modelAndView.addObject("salarySlipsGraph", salarySlipDTOs);

            if (groupedSalarySlips.isEmpty()) {
                modelAndView.addObject("info", "Aucune donnée disponible pour l'année " + year);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des données: " + e.getMessage());
        }

        return modelAndView;
    }

    @GetMapping("/graph")
    public ModelAndView groupedSalarySummaryGraph(HttpSession session,
                                        @RequestParam(defaultValue = "#{T(java.time.LocalDate).now().getYear()}") String year) {
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        EnvoyeInformation.afficherName(session, modelAndView);
        EnvoyeInformation.setInfo(modelAndView, "Graphe", "pages/statistiques/graph");
        modelAndView.addObject("selectedYear", year);

        try {
            List<DataDto> salaryComponents = dataService.getAllData(session, "Salary Component").getData();
            Map<String, SalarySlip> groupedSalarySlips = statistiquesService.getSalarySlipsGroupedByMonth(session, year, salaryComponents);
            List<SalarySlip> salarySlipDtos=new ArrayList<>();
            for (Map.Entry<String, SalarySlip> entry : groupedSalarySlips.entrySet()) {
                SalarySlip salarySlipDto = entry.getValue();
                salarySlipDtos.add(salarySlipDto);

            }
            modelAndView.addObject("componentNames", salaryComponents.stream().map(DataDto::getName).collect(Collectors.toList()));

            List<String> moisList = groupedSalarySlips.values().stream()
                                        .map(slip -> slip.getMois())
                                        .collect(Collectors.toList());

            modelAndView.addObject("moisList", moisList);

            List<SalarySlipModele> salarySlipDTOs = groupedSalarySlips.values().stream()
                    .map(slip -> new SalarySlipModele(
                        slip.getMois(),
                        slip.getGrossPay(),
                        slip.getTotalDeduction(),
                        slip.getNetPay(),
                        slip.getComponentsDef()
                    )).collect(Collectors.toList());

            modelAndView.addObject("groupedSalarySlips", salarySlipDTOs);


            if (groupedSalarySlips.isEmpty()) {
                modelAndView.addObject("info", "Aucune donnée disponible pour l'année " + year);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            modelAndView.addObject("error", "Erreur lors de la récupération des données: " + e.getMessage());
        }

        return modelAndView;
    }

}
