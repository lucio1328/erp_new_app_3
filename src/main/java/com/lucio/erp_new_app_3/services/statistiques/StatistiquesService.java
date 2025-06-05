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
import com.lucio.erp_new_app_3.dtos.salary.SalarySlip;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlipFilter;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlipListResponse;
import com.lucio.erp_new_app_3.dtos.salary.SalaryTotalsResponse;
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
        if(year!=null){
            filter.setStartDate(year + "-01-01");
            filter.setEndDate(year + "-12-31");
        }

        List<SalarySlip> salarySlipDtos = salaryRegisterService.getSalarySlips(session, 0, 0, filter).getData();
        Map<String, List<SalarySlip>> grouped = new TreeMap<>();

        for (SalarySlip slip : salarySlipDtos) {
            String month = slip.getPostingDate().toString().substring(0, 7);
            grouped.computeIfAbsent(month, k -> new ArrayList<>()).add(slip);
        }

        Map<String, SalarySlip> consolidated = new TreeMap<>();
        for (Map.Entry<String, List<SalarySlip>> entry : grouped.entrySet()) {
            String month = entry.getKey();
            List<SalarySlip> slips = entry.getValue();
            SalarySlipListResponse salarySlipListResponse=new SalarySlipListResponse();
            salarySlipListResponse.setData(slips);

            List<SalarySlip> enriched = salaryRegisterService.getRapport(session,salarySlipListResponse).getData();

            enriched = salaryRegisterService.getComponents(enriched, dataDtos);

            SalarySlip monthlySlip = new SalarySlip();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
            YearMonth yearMonth = YearMonth.parse(month, formatter);
            monthlySlip.setPostingDate(yearMonth.atDay(1));
            monthlySlip.setMois(DateValidator.getMonthName(month));
            monthlySlip.setEnvoye(month);

            monthlySlip.setCurrency(slips.get(0).getCurrency());

            double totalGross = 0;
            double totalDeduction = 0;
            double totalNet = 0;
            List<Double> totalComponents = new ArrayList<>();
            for (int i = 0; i < dataDtos.size(); i++) totalComponents.add(0.0);

            SalaryTotalsResponse salaryTotalsResponse=new SalaryTotalsResponse(enriched, dataDtos);

            totalGross+=salaryTotalsResponse.getTotalGrossPay();
            totalDeduction+=salaryTotalsResponse.getTotalDeductions();
            totalNet+=salaryTotalsResponse.getTotalNetPay();
            totalComponents=salaryTotalsResponse.getComponentsSum();

            monthlySlip.setGrossPay(totalGross);
            monthlySlip.setTotalDeduction(totalDeduction);
            monthlySlip.setNetPay(totalNet);
            monthlySlip.setComponentsDef(totalComponents);

            consolidated.put(month, monthlySlip);
        }

        return consolidated;
    }
}
