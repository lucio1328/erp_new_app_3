package com.lucio.erp_new_app_3.services.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lucio.erp_new_app_3.dtos.company.Company;
import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.dtos.employee.Genre;
import com.lucio.erp_new_app_3.services.company.CompanyService;
import com.lucio.erp_new_app_3.services.employee.EmployeeService;
import com.lucio.erp_new_app_3.services.employee.GenreService;
import com.lucio.erp_new_app_3.utils.DateValidator;

@Service
public class CsvEmployeImporter {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private GenreService genreService;

    @Autowired
    private CompanyService companyService;

    public record ResultatImport(String message, List<String> erreurs, List<String> lignesErronees) {}

    public ResultatImport traiterCsvEmployes(MultipartFile fichierEmploye, String sessionCookie) {
        List<String> erreurs = new ArrayList<>();
        List<String> lignesErronees = new ArrayList<>();

        if (fichierEmploye.isEmpty()) {
            return new ResultatImport("Le fichier est vide.", List.of("Fichier vide"), lignesErronees);
        }

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(fichierEmploye.getInputStream(), StandardCharsets.UTF_8))) {

            String ligne;
            int numeroLigne = 0;

            while ((ligne = reader.readLine()) != null) {
                numeroLigne++;

                if (numeroLigne == 1) continue;

                String[] champs = ligne.split(",");
                if (champs.length < 7) {
                    erreurs.add("Ligne " + numeroLigne + " : format invalide (colonnes manquantes)");
                    lignesErronees.add(ligne);
                    continue;
                }

                String dateEmbauche = champs[4].trim();
                String dateNaissance = champs[5].trim();

                if (!DateValidator.isValidDate(dateEmbauche)) {
                    erreurs.add("Ligne " + numeroLigne + " : date embauche invalide (" + dateEmbauche + ")");
                    lignesErronees.add(ligne);
                    continue;
                }

                if (!DateValidator.isValidDate(dateNaissance)) {
                    erreurs.add("Ligne " + numeroLigne + " : date naissance invalide (" + dateNaissance + ")");
                    lignesErronees.add(ligne);
                    continue;
                }

                String genre = champs[3];
                String company = champs[6];

                if (genreService.getByName(genre, sessionCookie) == null) {
                    Genre newGenre = new Genre();
                    newGenre.setName(genre);
                    newGenre.setGender(genre);

                    genreService.create(newGenre, sessionCookie);
                }

                if (companyService.getByName(company, sessionCookie) == null) {
                    Company newCompany = new Company();
                    newCompany.setName(company);
                    newCompany.setCompanyName(company);
                    newCompany.setAbbr(generateAbbreviation(company));
                    newCompany.setDefaultCurrency("EURO");
                    newCompany.setCountry("Madagascar");

                    companyService.create(newCompany, sessionCookie);
                }
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

                Employee employee = new Employee();
                employee.setLastName(champs[1]);
                employee.setFirstName(champs[2]);
                employee.setGender(genre);

                LocalDate dateE = LocalDate.parse(dateEmbauche, formatter);
                employee.setDateOfJoining(dateE);

                LocalDate dateN = LocalDate.parse(dateNaissance, formatter);
                employee.setDateOfBirth(dateN);

                employee.setCompany(company);
                employee.setStatus("Active");

                employeeService.create(employee, sessionCookie);

            }

        }
        catch (IOException e) {
            return new ResultatImport("Erreur de lecture du fichier.", List.of(e.getMessage()), lignesErronees);
        }

        String message = erreurs.isEmpty() ? "Import réussi !" : "Import terminé avec des erreurs.";
        return new ResultatImport(message, erreurs, lignesErronees);
    }

    public static String generateAbbreviation(String companyName) {
        return Arrays.stream(companyName.split(" "))
                    .filter(word -> !word.isEmpty())
                    .map(word -> word.substring(0, 1).toUpperCase())
                    .collect(Collectors.joining());
    }


}
