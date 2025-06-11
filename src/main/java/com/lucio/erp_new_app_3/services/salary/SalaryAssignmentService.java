package com.lucio.erp_new_app_3.services.salary;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.configs.ErpnextProperties;
import com.lucio.erp_new_app_3.dtos.salary.assignment.StructureAssignement;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

import jakarta.servlet.http.HttpSession;

@Service
public class SalaryAssignmentService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ErpnextProperties erpnextProperties;

    @Autowired
    private PreparationApi preparationApi;

    @Autowired
    private ObjectMapper objectMapper;

    public SalaryAssignmentService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public StructureAssignement getLatestAssignmentBeforeDate(String employeeId, HttpSession sessionCookie, YearMonth yearMonthLimit) {
        String sid = (String) sessionCookie.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }
        List<StructureAssignement> assignments = getSalaryAssignmentsByEmployee(employeeId, sid);

        LocalDate dateLimit = yearMonthLimit.atEndOfMonth();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        Optional<StructureAssignement> latest = assignments.stream()
            .filter(assign -> {
                try {
                    LocalDate fromDate = LocalDate.parse(assign.getFrom_date(), formatter);
                    return !fromDate.isAfter(dateLimit);
                }
                catch (Exception e) {
                    return false;
                }
            })
            .max(Comparator.comparing(assign -> LocalDate.parse(assign.getFrom_date(), formatter)));

        return latest.orElseThrow(() -> new ErpApiException(
            "Aucune structure d'assignation trouvée pour l'employé " + employeeId + " avant le mois " + yearMonthLimit,
            HttpStatus.NOT_FOUND.value()
        ));
    }

    public List<StructureAssignement> getSalaryAssignmentsByEmployee(String employeeId, String sessionCookie) {
        String endpoint = "/api/resource/Salary Structure Assignment?fields=[\"*\"]&filters=[[\"employee\",\"=\",\"" + employeeId + "\"]]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(StructureAssignement.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des structures assignments pour l'employé " + employeeId,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public void assignSalaryStructure(HttpSession session,StructureAssignement request) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        String url = erpnextProperties.getUrl() + "/api/resource/Salary Structure Assignment";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers = preparationApi.buildApiHeaders();

        HttpEntity<StructureAssignement> entity = new HttpEntity<>(request, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Échec d’assignation pour " + request.getEmployee());
        }
    }
}
