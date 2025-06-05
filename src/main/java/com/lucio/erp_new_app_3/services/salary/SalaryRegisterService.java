package com.lucio.erp_new_app_3.services.salary;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.configs.ErpnextProperties;
import com.lucio.erp_new_app_3.dtos.data.DataDto;
import com.lucio.erp_new_app_3.dtos.salary.SalaryDeduction;
import com.lucio.erp_new_app_3.dtos.salary.SalaryEarning;
import com.lucio.erp_new_app_3.dtos.salary.SalaryReportResponse;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlip;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlipDetail;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlipFilter;
import com.lucio.erp_new_app_3.dtos.salary.SalarySlipListResponse;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SalaryRegisterService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private ErpnextProperties erpnextProperties;

    public SalaryRegisterService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public SalaryReportResponse getSalaryRegisterReport(String sessionCookie) {
        try {
            String url = "/api/method/frappe.desk.query_report.run";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("report_name", "Salary Register");

            String jsonBody = objectMapper.writeValueAsString(requestBody);
            JsonNode response = preparationApi.postJsonDataToApiWithMessage(url, jsonBody, sessionCookie);
            return SalaryReportResponse.fromJson(response, objectMapper, salarySlipService, sessionCookie);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la récupération du rapport Salary Register",
                    HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }


    public String fields(){
        return
        "[\"*\"]";
    }

    public SalarySlipListResponse getSalarySlips(HttpSession session, int start, int pageLength, SalarySlipFilter filter) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session non authentifiée");
        }

        String fields = fields();

        String filtersParam = buildFilters(filter);

        StringBuilder urlBuilder = new StringBuilder(erpnextProperties.getUrl() + "/api/resource/Salary Slip?");

        boolean hasDateFilter = filter != null
            && ( (filter.getStartDate() != null && !filter.getStartDate().isEmpty())
            || (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) );

        if (!hasDateFilter) {
            urlBuilder.append("limit_start=").append(start)
                        .append("&limit_page_length=").append(pageLength);
        }

        urlBuilder.append("&fields=").append(fields);

        if (!filtersParam.isEmpty()) {
            urlBuilder.append(filtersParam);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<SalarySlipListResponse> response = restTemplate.exchange(
                urlBuilder.toString(),
                HttpMethod.GET,
                request,
                SalarySlipListResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            } else {
                throw new RuntimeException("Échec de la récupération des fiches de paie : " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de la récupération des fiches de paie : " + e.getMessage(), e);
        }
    }

    private String buildFilters(SalarySlipFilter filter) {
        if (filter == null) return "";

        StringBuilder filters = new StringBuilder("&filters=[");

        boolean hasFilter = false;

        if (filter.getEmployee() != null && !filter.getEmployee().isEmpty()) {
            filters.append("[\"employee\", \"=\", \"").append(filter.getEmployee()).append("\"],");
            hasFilter = true;
        }
        if (filter.getStartDate() != null && !filter.getStartDate().isEmpty()) {
            filters.append("[\"start_date\", \">=\", \"").append(filter.getStartDate()).append("\"],");
            hasFilter = true;
        }
        if (filter.getEndDate() != null && !filter.getEndDate().isEmpty()) {
            filters.append("[\"end_date\", \"<=\", \"").append(filter.getEndDate()).append("\"],");
            hasFilter = true;
        }

        if (hasFilter) {
            filters.setLength(filters.length() - 1);
            filters.append("]");
            return filters.toString();
        }

        return "";
    }


    public SalarySlipListResponse getRapport(HttpSession session,SalarySlipListResponse salarySlipListResponse){
        List<SalarySlip> salarySlipDtos=salarySlipListResponse.getData();
        SalarySlipListResponse salarySlipListResponse2=new SalarySlipListResponse();
        List<SalarySlip> salarySlipDtos2=new ArrayList<>();
        for (SalarySlip salarySlipDto : salarySlipDtos) {
            SalarySlip salarySlipDto2 = getSalarySlipByName(session, salarySlipDto.getName()).getData();
            salarySlipDtos2.add(salarySlipDto2);
        }
        salarySlipListResponse2.setData(salarySlipDtos2);
        return salarySlipListResponse2;

    }

    public List<SalarySlip> getComponents(List<SalarySlip> salarySlipDtos, List<DataDto> dataDtos) {
        List<SalarySlip> salarySlipDtos2 = new ArrayList<>();

        for (SalarySlip salarySlipDto : salarySlipDtos) {
            List<Double> componentsData = new ArrayList<>();

            for (DataDto dataDto : dataDtos) {
                boolean found = false;

                // Recherche dans les earnings
                for (SalaryEarning salaryEarning : salarySlipDto.getEarnings()) {
                    if (salaryEarning.getSalaryComponent().equals(dataDto.getName())) {
                        componentsData.add(salaryEarning.getAmount());
                        found = true;
                        break;
                    }
                }

                // Recherche dans les deductions si non trouvé dans earnings
                if (!found) {
                    for (SalaryDeduction salaryDeduction : salarySlipDto.getDeductions()) {
                        if (salaryDeduction.getSalaryComponent().equals(dataDto.getName())) {
                            componentsData.add(salaryDeduction.getAmount());
                            found = true;
                            break;
                        }
                    }
                }

                // Si toujours pas trouvé, ajouter 0
                if (!found) {
                    componentsData.add(0.0);
                }
            }

            salarySlipDto.setComponentsDef(componentsData);
            salarySlipDtos2.add(salarySlipDto);
        }

        return salarySlipDtos2;
    }

    public SalarySlipDetail getSalarySlipByName(HttpSession session, String name) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }

        String url = erpnextProperties.getUrl() + "/api/resource/Salary Slip/" + name;

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<SalarySlipDetail> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                SalarySlipDetail.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            else {
                throw new RuntimeException("Salary slip not found: " + response.getStatusCode());
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error while fetching salary slip: " + e.getMessage(), e);
        }
    }
}
