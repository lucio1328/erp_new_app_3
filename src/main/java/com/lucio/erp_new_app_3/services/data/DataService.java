package com.lucio.erp_new_app_3.services.data;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.lucio.erp_new_app_3.configs.ErpnextProperties;
import com.lucio.erp_new_app_3.dtos.data.DataListReponse;

import jakarta.servlet.http.HttpSession;

@Service
public class DataService {
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ErpnextProperties erpnextProperties;

    @SuppressWarnings("null")
    public DataListReponse getAllData(HttpSession session,String type) {
        String sid = (String) session.getAttribute("sid");
        if (sid == null || sid.isEmpty()) {
            throw new RuntimeException("Session not authenticated");
        }
        String url=null;
        if(type.equals("Designation")){
            url = erpnextProperties.getUrl() + "/api/resource/Designation";
        }
        else if(type.equals("Department")){
            url = erpnextProperties.getUrl() + "/api/resource/Department";
        }
        else if(type.equals("Company")){
            url = erpnextProperties.getUrl() + "/api/resource/Company";
        }
        else if(type.equals("Salary Component")){
            url = erpnextProperties.getUrl() + "/api/resource/Salary Component";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add("Cookie", "sid=" + sid);
        // headers.set("Authorization", "token " + erpnextApiKey + ":" + erpnextApiSecret);

        HttpEntity<String> request = new HttpEntity<>(headers);

        try {
            ResponseEntity<DataListReponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                request,
                DataListReponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return response.getBody();
            }
            else {
                throw new RuntimeException("Failed to fetch designations: " + response.getStatusCode());
            }

        }
        catch (Exception e) {
            throw new RuntimeException("Error while fetching designations: " + e.getMessage(), e);
        }
    }

}
