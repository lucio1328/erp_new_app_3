package com.lucio.erp_new_app_3.services.salary;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.configs.ErpnextProperties;
import com.lucio.erp_new_app_3.dtos.alea.ModifSalaire;
import com.lucio.erp_new_app_3.dtos.imports.SalaireData;
import com.lucio.erp_new_app_3.dtos.salary.assignment.StructureAssignement;
import com.lucio.erp_new_app_3.dtos.salary.assignment.StructureDetail;
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

    public SalaireData creerSalaireData(StructureAssignement structureAssignement, ModifSalaire modifSalaire) {
        SalaireData data = new SalaireData();
        data.setMois(structureAssignement.getFrom_date());
        data.setRefEmploye(structureAssignement.getEmployee());

        double base = structureAssignement.getBase();
        double pourcentage = Double.parseDouble(modifSalaire.getValPourcentage());

        double newAmount = modifSalaire.getPourcentage().equals("plus")
                            ? base * (1 + pourcentage / 100)
                            : base * (1 - pourcentage / 100);

        data.setSalaireBase(newAmount);
        data.setSalaryStructure(structureAssignement.getSalary_structure());

        return data;
    }

    public StructureAssignement supprimerAttribution(String sessionCookie, String empId, String startDate) {
        LocalDate date = LocalDate.parse(startDate);

        Optional<StructureAssignement> optionalAssignment =
            getLatestAssignmentBeforeDate(empId, sessionCookie, date);

        if (optionalAssignment.isPresent()) {
            StructureAssignement assignment = optionalAssignment.get();
            deleteSalaryAssignment(assignment.getName(), sessionCookie);

            return assignment;
        }
        else {
            throw new ErpApiException(
                "Aucune assignation trouvée pour l'employé " + empId + " avant la date " + startDate,
                HttpStatus.NOT_FOUND.value()
            );
        }
    }

    public void deleteSalaryAssignment(String name, String sessionCookie) {
        try {
            String endpoint = "/api/resource/Salary Structure Assignment/" + name;
            preparationApi.deleteDataFromApi(endpoint, sessionCookie);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la suppression de l'attribution salariale pour " + name,
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public StructureAssignement annulerAttribution(String sessionCookie, String empId, String startDate) {
        LocalDate date = LocalDate.parse(startDate);

        Optional<StructureAssignement> optionalAssignment =
            getLatestAssignmentBeforeDate(empId, sessionCookie, date);

        if (optionalAssignment.isPresent()) {
            StructureAssignement assignment = optionalAssignment.get();

            if (assignment.getDocstatus() == 1) {
                cancelSalaryAssignment(assignment.getName(), sessionCookie);
            }

            return assignment;
        }
        else {
            throw new ErpApiException(
                "Aucune assignation trouvée pour l'employé " + empId + " avant la date " + startDate,
                HttpStatus.NOT_FOUND.value()
            );
        }
    }

    public void cancelSalaryAssignment(String name, String sessionCookie) {
        try {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException("Le nom du Salary Structure Assignment ne peut pas être vide");
            }

            HttpHeaders headers = new HttpHeaders();
            headers = preparationApi.buildApiHeaders();

            String url = erpnextProperties.getUrl() + "/api/resource/Salary Structure Assignment/" + name;

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("docstatus", 2);

            HttpEntity<Map<String, Object>> cancel = new HttpEntity<>(requestBody, headers);
            ResponseEntity<String> cancelResponse = restTemplate.exchange(url, HttpMethod.PUT, cancel, String.class);
            if (cancelResponse.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Echec de l'annulation de l'attribution salariale");
            }

        }
        catch (IllegalArgumentException e) {
            throw new ErpApiException(e.getMessage(), HttpStatus.BAD_REQUEST.value(), e);
        }
        catch (Exception e) {
            throw new ErpApiException("Échec de l'annulation du Salary Structure Assignment",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public Optional<StructureAssignement> getLatestAssignmentBeforeDate(String employeeId, String sid, LocalDate fallbackDate) {
        List<StructureAssignement> assignments = getSalaryAssignmentsByEmployee(employeeId, sid);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return assignments.stream()
            .filter(assign -> {
                try {
                    LocalDate fromDate = LocalDate.parse(assign.getFrom_date(), formatter);
                    return !fromDate.isAfter(fallbackDate);
                } catch (Exception e) {
                    return false;
                }
            })
            .max(Comparator.comparing(assign -> {
                try {
                    return LocalDate.parse(assign.getFrom_date(), formatter);
                }
                catch (Exception e) {
                    return LocalDate.MIN;
                }
            }));
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

    public void assignSalaryStructureBloc(HttpSession session,List<StructureAssignement> structureAssignements){
        for (StructureAssignement structureAssignement : structureAssignements) {
            assignSalaryStructure(session, structureAssignement);
        }
    }


    public void assignToSalaryStructures(HttpSession session,String salary_structure,String company,String from_date,String currency,List<StructureDetail> structureDetails){
        List<StructureAssignement> structureAssignements=new ArrayList<>();
        for (StructureDetail structureDetail : structureDetails) {
            structureAssignements.add(new StructureAssignement(company, salary_structure, currency, structureDetail.getEmployee(), from_date, structureDetail.getBase(), structureDetail.getVariable()));
        }
        assignSalaryStructureBloc(session, structureAssignements);
    }
}
