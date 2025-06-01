package com.lucio.erp_new_app_3.services.employee;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lucio.erp_new_app_3.dtos.employee.Genre;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.utils.PreparationApi;

@Service
public class GenreService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PreparationApi preparationApi;

    public GenreService(PreparationApi preparationApi) {
        this.preparationApi = preparationApi;
    }

    public List<Genre> getAllGenres(String sessionCookie) {
        String endpoint = "/api/resource/Gender?fields=[\"*\"]";
        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);
        try {
            return objectMapper.readerForListOf(Genre.class).readValue(data);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing des genres", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public Genre getByName(String name, String sessionCookie) {
        String endpoint = "/api/resource/Gender/" + name;

        JsonNode data = preparationApi.getJsonDataFromApi(endpoint, sessionCookie);

        try {
            return objectMapper.treeToValue(data, Genre.class);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur de parsing du genre " + name, HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

    public Genre create(Genre genre, String sessionCookie) {
        String endpoint = "/api/resource/Gender";
        try {
            String jsonBody = objectMapper.writeValueAsString(genre);
            JsonNode response = preparationApi.postJsonDataToApi(endpoint, jsonBody, sessionCookie);
            JsonNode dataNode = response.get("data");

            if (dataNode == null || dataNode.isNull()) {
                throw new ErpApiException("Erreur lors de la création du genre", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            return objectMapper.treeToValue(dataNode, Genre.class);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la création du genre", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

}
