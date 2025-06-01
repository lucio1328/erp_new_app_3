package com.lucio.erp_new_app_3.services.salary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlip;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

@Service
public class SalarySlipService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public SalarySlipService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public List<SalarySlip> getSalarySlipsByEmployee(String employeeId, String sessionCookie) {
        String endpoint = "/api/resource/Salary Slip?fields=[\"*\"]&filters=[[\"employee\",\"=\",\"" + employeeId + "\"]]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(SalarySlip.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des fiches de paie pour l'employé " + employeeId,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public SalarySlip getSalarySlip(String name, String sessionCookie) {
        try {
            String endpoint = "/api/resource/Salary Slip?fields=[\"*\"]&filters=[[\"name\",\"=\",\"" + name + "\"]]";
            JsonNode response = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);

            return objectMapper.treeToValue(response.get(0), SalarySlip.class);

        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing de la fiche de paie pour " + name,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public List<SalarySlip> getSalarySlipsByMonthYear(int month, int year, String sessionCookie) {
        try {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDateStr = startDate.format(formatter);
            String endDateStr = endDate.format(formatter);

            String endpoint = "/api/resource/Salary Slip?fields=[\"*\"]&filters=[[\"posting_date\",\">=\",\"" + startDateStr + "\"],[\"posting_date\",\"<=\",\"" + endDateStr + "\"]]";
            JsonNode response = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);

            return objectMapper.readerForListOf(SalarySlip.class).readValue(response);

        }
        catch (Exception e) {
            throw new ErpApiException(
                String.format("Erreur lors de la récupération des fiches de paie pour %d/%d", month, year),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e
            );
        }
    }

    public List<SalarySlip> getSalarySlipsByEmployeeAndMonthYear(String employeeId, int month, int year, String sessionCookie) {
        try {
            LocalDate startDate = LocalDate.of(year, month, 1);
            LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String startDateStr = startDate.format(formatter);
            String endDateStr = endDate.format(formatter);

            String endpoint = "/api/resource/Salary Slip"
                    + "?fields=[\"*\"]"
                    + "&filters=["
                    + "[\"employee\", \"=\", \"" + employeeId + "\"],"
                    + "[\"posting_date\", \">=\", \"" + startDateStr + "\"],"
                    + "[\"posting_date\", \"<=\", \"" + endDateStr + "\"]"
                    + "]&order_by=posting_date desc";

            JsonNode response = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
            return objectMapper.readerForListOf(SalarySlip.class).readValue(response);
        }
        catch (Exception e) {
            throw new ErpApiException(
                String.format("Erreur lors de la récupération des fiches de paie pour l'employé %s en %d/%d", employeeId, month, year),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e
            );
        }
    }

    public List<SalarySlip> getSalarySlipsByEmployeeWithFilters(String employeeId, String monthStr, String yearStr, String sessionCookie) {
        try {
            String endpointBase = "/api/resource/Salary Slip?fields=[\"*\"]&filters=[[\"employee\",\"=\",\"" + employeeId + "\"]";
            String filtersDate = "";

            boolean filterByMonth = monthStr != null && !monthStr.equalsIgnoreCase("Tous");
            boolean filterByYear = yearStr != null && !yearStr.equalsIgnoreCase("Toutes");

            if (filterByMonth && filterByYear) {
                int month = Integer.parseInt(monthStr);
                int year = Integer.parseInt(yearStr);
                LocalDate startDate = LocalDate.of(year, month, 1);
                LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                filtersDate = ",[\"posting_date\", \">=\", \"" + startDate.format(formatter) + "\"],"
                            + "[\"posting_date\", \"<=\", \"" + endDate.format(formatter) + "\"]";
            }
            else if (filterByMonth) {
                filtersDate = "";
            }
            else if (filterByYear) {
                int year = Integer.parseInt(yearStr);
                LocalDate startDate = LocalDate.of(year, 1, 1);
                LocalDate endDate = LocalDate.of(year, 12, 31);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                filtersDate = ",[\"posting_date\", \">=\", \"" + startDate.format(formatter) + "\"],"
                            + "[\"posting_date\", \"<=\", \"" + endDate.format(formatter) + "\"]";
            }

            String endpoint = endpointBase + filtersDate + "]&order_by=posting_date desc";
            JsonNode response = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
            List<SalarySlip> salarySlips = objectMapper.readerForListOf(SalarySlip.class).readValue(response);

            if (filterByMonth && !filterByYear) {
                int month = Integer.parseInt(monthStr);
                salarySlips = salarySlips.stream()
                    .filter(slip -> slip.getPostingDate() != null && slip.getPostingDate().getMonthValue() == month)
                    .collect(Collectors.toList());
            }

            return salarySlips;

        }
        catch (Exception e) {
            throw new ErpApiException(
                String.format("Erreur lors de la récupération des fiches de paie pour l'employé %s avec mois %s et année %s", employeeId, monthStr, yearStr),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e
            );
        }
    }

}
