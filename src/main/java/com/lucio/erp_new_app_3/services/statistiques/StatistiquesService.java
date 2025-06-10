package com.lucio.erp_new_app_3.services.statistiques;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucio.erp_new_app_3.dtos.data.DataDto;
import com.lucio.erp_new_app_3.dtos.salary.SalaryTotalsResponse;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlip;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlipFilter;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlipListResponse;
import com.lucio.erp_new_app_3.services.salary.SalaryRegisterService;
import com.lucio.erp_new_app_3.utils.DateValidator;

import jakarta.servlet.http.HttpSession;

@Service
public class StatistiquesService {

    @Autowired
    private SalaryRegisterService salaryRegisterService;

    public Map<String, SalarySlip> getSalarySlipsGroupedByMonth(HttpSession session, String year, List<DataDto> dataDtos) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        SalarySlipFilter filter = new SalarySlipFilter();
        if (year != null) {
            filter.setStartDate(year + "-01-01");
            filter.setEndDate(year + "-12-31");
        }

        List<SalarySlip> salarySlipDtos = salaryRegisterService.getSalarySlips(session, 0, 0, filter).getData();
        Map<String, List<SalarySlip>> grouped = new TreeMap<>();

        String month = "";
        for (SalarySlip slip : salarySlipDtos) {
            month = slip.getPostingDate().toString().substring(0, 7);
            grouped.computeIfAbsent(month, k -> new ArrayList<>()).add(slip);
        }

        Map<String, SalarySlip> consolidated = new TreeMap<>();

        for (int monthNum = 1; monthNum <= 12; monthNum++) {
            String monthStr = String.format("%s-%02d", year, monthNum);
            List<SalarySlip> slips = grouped.get(monthStr);

            SalarySlip monthlySlip = new SalarySlip();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(month, formatter);
            monthlySlip.setPostingDate(yearMonth.atDay(1));
            monthlySlip.setMois(DateValidator.getMonthName(monthStr));

            if (slips == null || slips.isEmpty()) {
                // Valeurs par défaut si aucun bulletin ce mois
                monthlySlip.setCurrency("EURO"); // ou autre valeur par défaut
                monthlySlip.setGrossPay(0.0);
                monthlySlip.setTotalDeduction(0.0);
                monthlySlip.setNetPay(0.0);

                List<Double> componentsZeros = new ArrayList<>();
                for (int i = 0; i < dataDtos.size(); i++) {
                    componentsZeros.add(0.0);
                }
                monthlySlip.setComponentsDef(componentsZeros);
            }
            else {
                SalarySlipListResponse salarySlipListResponse = new SalarySlipListResponse();
                salarySlipListResponse.setData(slips);

                List<SalarySlip> enriched = salaryRegisterService.getRapport(session, salarySlipListResponse).getData();
                enriched = salaryRegisterService.getComponents(enriched, dataDtos);

                SalaryTotalsResponse salaryTotalsResponse = new SalaryTotalsResponse(enriched, dataDtos);

                monthlySlip.setCurrency(slips.get(0).getCurrency());
                monthlySlip.setGrossPay(salaryTotalsResponse.getTotalGrossPay());
                monthlySlip.setTotalDeduction(salaryTotalsResponse.getTotalDeductions());
                monthlySlip.setNetPay(salaryTotalsResponse.getTotalNetPay());
                monthlySlip.setComponentsDef(salaryTotalsResponse.getComponentsSum());
            }

            consolidated.put(monthStr, monthlySlip);
        }

        return consolidated;
    }

    // public Map<String, SalarySlip> getSalarySlipsGroupedByMonth(HttpSession session, String year, List<DataDto> dataDtos) {
    //     String sid = (String) session.getAttribute("sid");
    //     if (sid == null || sid.isEmpty()) {
    //         throw new RuntimeException("Session non authentifiée");
    //     }

    //     SalarySlipFilter filter = new SalarySlipFilter();
    //     if(year!=null){
    //         filter.setStartDate(year + "-01-01");
    //         filter.setEndDate(year + "-12-31");
    //     }

    //     List<SalarySlip> salarySlipDtos = salaryRegisterService.getSalarySlips(session, 0, 0, filter).getData();
    //     Map<String, List<SalarySlip>> grouped = new TreeMap<>();

    //     for (SalarySlip slip : salarySlipDtos) {
    //         String month = slip.getPostingDate().toString().substring(0, 7);
    //         grouped.computeIfAbsent(month, k -> new ArrayList<>()).add(slip);
    //     }

    //     Map<String, SalarySlip> consolidated = new TreeMap<>();
    //     for (Map.Entry<String, List<SalarySlip>> entry : grouped.entrySet()) {
    //         String month = entry.getKey();
    //         List<SalarySlip> slips = entry.getValue();
    //         SalarySlipListResponse salarySlipListResponse=new SalarySlipListResponse();
    //         salarySlipListResponse.setData(slips);

    //         List<SalarySlip> enriched = salaryRegisterService.getRapport(session,salarySlipListResponse).getData();

    //         enriched = salaryRegisterService.getComponents(enriched, dataDtos);

    //         SalarySlip monthlySlip = new SalarySlip();

    //         DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
    //         YearMonth yearMonth = YearMonth.parse(month, formatter);
    //         monthlySlip.setPostingDate(yearMonth.atDay(1));
    //         monthlySlip.setMois(DateValidator.getMonthName(month));
    //         monthlySlip.setEnvoye(month);

    //         monthlySlip.setCurrency(slips.get(0).getCurrency());

    //         double totalGross = 0;
    //         double totalDeduction = 0;
    //         double totalNet = 0;
    //         List<Double> totalComponents = new ArrayList<>();
    //         for (int i = 0; i < dataDtos.size(); i++) totalComponents.add(0.0);

    //         SalaryTotalsResponse salaryTotalsResponse=new SalaryTotalsResponse(enriched, dataDtos);

    //         totalGross+=salaryTotalsResponse.getTotalGrossPay();
    //         totalDeduction+=salaryTotalsResponse.getTotalDeductions();
    //         totalNet+=salaryTotalsResponse.getTotalNetPay();
    //         totalComponents=salaryTotalsResponse.getComponentsSum();

    //         monthlySlip.setGrossPay(totalGross);
    //         monthlySlip.setTotalDeduction(totalDeduction);
    //         monthlySlip.setNetPay(totalNet);
    //         monthlySlip.setComponentsDef(totalComponents);

    //         consolidated.put(month, monthlySlip);
    //     }

    //     return consolidated;
    // }
}
