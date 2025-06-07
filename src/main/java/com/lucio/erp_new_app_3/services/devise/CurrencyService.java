package com.lucio.erp_new_app_3.services.devise;

import java.util.Currency;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

@Service
public class CurrencyService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public CurrencyService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public List<Currency> getAllCurrency(String sessionCookie) {
        String endpoint = "/api/resource/Currency?fields=[\"*\"]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(Currency.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des devises", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }
}
