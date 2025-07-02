package com.lucio.erp_new_app_3.services.salary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.configs.ErpnextProperties;
import com.lucio.erp_new_app_3.dtos.alea.GenereSalaire;
import com.lucio.erp_new_app_3.dtos.salary.details_salary.SalaryDeduction;
import com.lucio.erp_new_app_3.dtos.salary.details_salary.SalaryEarning;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlip;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

import jakarta.servlet.http.HttpSession;

@Service
public class SalarySlipService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ErpnextProperties erpnextProperties;

    public SalarySlipService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public void submitSalarySlip(String name) {
        try {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Le nom du Salary Slip ne peut pas être vide");
            }

            HttpHeaders headers = new HttpHeaders();
            headers = preparationApi.buildApiHeaders();

            String url = erpnextProperties.getUrl() + "/api/resource/Salary Slip/" + name;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("docstatus", 1);

            HttpEntity<Map<String, Object>> cancel = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> cancelResponse = restTemplate.exchange(url, HttpMethod.PUT, cancel, String.class);
            if (cancelResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Echec de validation de fiche de paie");
            }

        }
        catch (IllegalArgumentException e) {
            throw new ErpApiException(e.getMessage(), HttpStatus.BAD_REQUEST.value(), e);
        }
        catch (Exception e) {
            throw new ErpApiException("Échec de validation du Salary Slip",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    @SuppressWarnings("unchecked")
    public SalarySlip createSalarySlip(SalarySlip salarySlip, String sessionCookie) {
        String url = erpnextProperties.getUrl() + "/api/resource/Salary Slip";
        HttpHeaders headers = new HttpHeaders();
        headers = preparationApi.buildApiHeaders();

        Map<String, Object> payload = new HashMap<>();
        payload.put("employee", salarySlip.getEmployee());
        payload.put("start_date", salarySlip.getStartDate());
        payload.put("end_date", salarySlip.getEndDate());
        payload.put("salary_structure", salarySlip.getSalaryStructure());
        payload.put("company", salarySlip.getCompany());
        payload.put("currency", salarySlip.getCurrency());

        payload.put("salary_slip_based_on_timesheet", 0);
        payload.put("__islocal", 1);
        payload.put("__unsaved", 1);

        List<Map<String, Object>> earnings = new ArrayList<>();
        for (SalaryEarning earning : salarySlip.getEarnings()) {
            Map<String, Object> e = new HashMap<>();
            e.put("salary_component", earning.getSalaryComponent());
            e.put("amount", earning.getAmount());
            e.put("doctype", "Salary Detail");
            e.put("default_amount", earning.getAmount());
            e.put("amount_based_on_formula", 0);
            e.put("__islocal", 1);
            earnings.add(e);
        }
        payload.put("earnings", earnings);

        List<Map<String, Object>> deductions = new ArrayList<>();
        for (SalaryDeduction deduction : salarySlip.getDeductions()) {
            Map<String, Object> d = new HashMap<>();
            d.put("salary_component", deduction.getSalaryComponent());
            d.put("amount", deduction.getAmount());
            d.put("doctype", "Salary Detail");
            deductions.add(d);
        }
        payload.put("deductions", deductions);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> responseMap = mapper.readValue(response.getBody(), Map.class);
            Map<String, Object> data = (Map<String, Object>) responseMap.get("data");

            SalarySlip createdSlip = new SalarySlip();
            createdSlip.setName((String) data.get("name"));
            return createdSlip;
        }
        catch (Exception e) {
            throw new RuntimeException("Erreur création Salary Slip", e);
        }
    }

    public void cancelSalarySlip(String name, String sessionCookie) {
        try {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Le nom du Salary Slip ne peut pas être vide");
            }

            HttpHeaders headers = new HttpHeaders();
            headers = preparationApi.buildApiHeaders();

            String url = erpnextProperties.getUrl() + "/api/resource/Salary Slip/" + name;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("docstatus", 2);

            HttpEntity<Map<String, Object>> cancel = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> cancelResponse = restTemplate.exchange(url, HttpMethod.PUT, cancel, String.class);
            if (cancelResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Echec de l'annulation de fiche de paie");
            }

        }
        catch (IllegalArgumentException e) {
            throw new ErpApiException(e.getMessage(), HttpStatus.BAD_REQUEST.value(), e);
        }
        catch (Exception e) {
            throw new ErpApiException("Échec de l'annulation du Salary Slip",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
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

    public List<SalarySlip> getSalarySlips(String sessionCookie) {
        String endpoint = "/api/resource/Salary Slip?fields=[\"*\"]&limit_page_length=100&limit_start=0";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(SalarySlip.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des fiches de paie ",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public void deleteSalarySlip(String name, String sessionCookie) {
        try {
            String endpoint = "/api/resource/Salary Slip/" + name;
            preparationApi.deleteDataFromApi(endpoint, sessionCookie);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la suppression de la fiche de paie pour " + name,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public SalarySlip getSalarySlip(String name, String sessionCookie) {
        try {
            String endpoint = "/api/resource/Salary Slip/" + name;
            JsonNode response = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);

            return objectMapper.treeToValue(response, SalarySlip.class);

        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing de la fiche de paie pour " + name,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public SalarySlip isSalarySlipExiste(String session, String employeeId, LocalDate forDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<SalarySlip> salarySlips = getSalarySlips(session);

        for (SalarySlip slip : salarySlips) {
            if (!slip.getEmployee().equals(employeeId)) {
                continue;
            }

            LocalDate startDate = LocalDate.parse(slip.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(slip.getEndDate(), formatter);

            if ((forDate.isEqual(startDate) || forDate.isAfter(startDate)) &&
                (forDate.isEqual(endDate) || forDate.isBefore(endDate))) {
                return slip;
            }
        }

        return null;
    }

    public SalarySlip verifierSalarySlips(GenereSalaire genereSalaire, LocalDate startDate, LocalDate endDate, String sessionCookie) {
        try {
            String endpoint = "/api/resource/Salary Slip?fields=[\"*\"]&filters=[[\"posting_date\",\">=\",\"" + startDate + "\"],[\"posting_date\",\"<=\",\"" + endDate + "\"],[\"employee\",\"=\",\"" + genereSalaire.getEmploye() + "\"]]";
            JsonNode response = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);

            return objectMapper.readerForListOf(SalarySlip.class).readValue(response);

        }
        catch (Exception e) {
            throw new ErpApiException(
                String.format("Erreur lors de la récupération des fiches de paie entre %s et %s", startDate, endDate),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                e
            );
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
