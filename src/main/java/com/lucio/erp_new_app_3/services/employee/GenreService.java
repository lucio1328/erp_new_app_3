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
        List<Genre> genres = getAllGenres(sessionCookie);

        return genres.stream()
            .filter(g -> g.getName() != null && g.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElse(null);
    }

    public Genre create(Genre genre, String sessionCookie) {
        String endpoint = "/api/resource/Gender";
        try {
            String jsonBody = objectMapper.writeValueAsString(genre);
            JsonNode response = preparationApi.postJsonDataToApi(endpoint, jsonBody, sessionCookie);

            if (response == null || response.isNull()) {
                throw new ErpApiException("Erreur lors de la création du genre", HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
            return objectMapper.treeToValue(response, Genre.class);
        }
        catch (Exception e) {
            throw new ErpApiException("Erreur lors de la création du genre", HttpStatus.INTERNAL_SERVER_ERROR.value(), e);
        }
    }

}
