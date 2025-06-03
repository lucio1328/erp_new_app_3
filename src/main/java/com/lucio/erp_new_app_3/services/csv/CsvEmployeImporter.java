package com.lucio.erp_new_app_3.services.csv;

import com.lucio.erp_new_app_3.utils.DateValidator;
import com.lucio.erp_new_app_3.dtos.employee.Employee;
import com.lucio.erp_new_app_3.dtos.employee.Genre;
import com.lucio.erp_new_app_3.dtos.company.Company;
import com.lucio.erp_new_app_3.dtos.csv.EmployeeCsvDto;
import com.lucio.erp_new_app_3.services.employee.EmployeeService;
import com.lucio.erp_new_app_3.services.employee.GenreService;
import com.lucio.erp_new_app_3.services.company.CompanyService;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CsvEmployeImporter {

    @Autowired private EmployeeService employeeService;
    @Autowired private GenreService    genreService;
    @Autowired private CompanyService  companyService;

    public record ResultatImport(String message,
                                List<String> erreurs,
                                List<String> lignesErronees) {}

    public ResultatImport traiterCsvEmployes(MultipartFile fichierEmploye, String sessionCookie) {

        if (fichierEmploye.isEmpty()) {
            return new ResultatImport(
                    "Le fichier est vide.",
                    List.of("Fichier vide"),
                    Collections.emptyList());
        }

        List<String> erreurs        = new ArrayList<>();
        List<String> lignesErronees = new ArrayList<>();
        List<Employee> employeesToCreate = new ArrayList<>();

        // ---------- 1. Lecture et mapping CSV -> DTO -----------------
        List<EmployeeCsvDto> records;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(fichierEmploye.getInputStream(), StandardCharsets.UTF_8))) {

            CsvToBean<EmployeeCsvDto> csvToBean = new CsvToBeanBuilder<EmployeeCsvDto>(reader)
                    .withType(EmployeeCsvDto.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withSkipLines(1)
                    .build();

            records = csvToBean.parse();

        } catch (Exception ex) {
            return new ResultatImport("Erreur de lecture du fichier.",
                    List.of(ex.getMessage()), Collections.emptyList());
        }

        // ---------- 2. Validation & préparation ----------------------
        int lineNumber = 1;
        for (EmployeeCsvDto dto : records) {
            lineNumber++;

            String dateEmbStr = dto.getDateEmbauche();
            String dateNaiStr = dto.getDateNaissance();

            if (!DateValidator.isValidDate(dateEmbStr)) {
                erreurs.add("Ligne " + lineNumber +
                            " : date embauche invalide (" + dateEmbStr + ")");
                lignesErronees.add(dtoToCsvLine(dto));
                continue;
            }

            if (!DateValidator.isValidDate(dateNaiStr)) {
                erreurs.add("Ligne " + lineNumber +
                            " : date naissance invalide (" + dateNaiStr + ")");
                lignesErronees.add(dtoToCsvLine(dto));
                continue;
            }

            manageGenre(dto.getGender(), sessionCookie);
            manageCompany(dto.getCompany(), sessionCookie);

            // Mapping DTO -> Employee
            Employee employee = new Employee();
            employee.setLastName(dto.getLastName());
            employee.setFirstName(dto.getFirstName());
            employee.setGender(dto.getGender());

            LocalDate dateEmb = DateValidator.normalizeToStandardFormat(dto.getDateEmbauche());
            LocalDate dateNai = DateValidator.normalizeToStandardFormat(dto.getDateNaissance());
            employee.setDateOfJoining(dateEmb);
            employee.setDateOfBirth(dateNai);

            employee.setCompany(dto.getCompany());
            employee.setStatus("Active");

            employeesToCreate.add(employee);
        }

        // ---------- 3. Si erreurs, on annule tout --------------------
        if (!erreurs.isEmpty()) {
            return new ResultatImport("Import annulé : erreurs détectées.",
                    erreurs, lignesErronees);
        }

        // ---------- 4. Insertion en base -----------------------------
        employeesToCreate.forEach(e -> employeeService.create(e, sessionCookie));

        return new ResultatImport("Import réussi !",
                Collections.emptyList(), Collections.emptyList());
    }

    /* ----------------------------------------------------------------- */
    /* ------------------------- Méthodes utilitaires ------------------ */

    private void manageGenre(String genre, String cookie) {
        if (genreService.getByName(genre, cookie) == null) {
            Genre g = new Genre();
            g.setName(genre);
            g.setGender(genre);
            genreService.create(g, cookie);
        }
    }

    private void manageCompany(String company, String cookie) {
        if (companyService.getByName(company, cookie) == null) {
            Company c = new Company();
            c.setName(company);
            c.setCompanyName(company);
            c.setAbbr(generateAbbreviation(company));
            c.setDefaultCurrency("EURO");
            c.setCountry("Madagascar");
            companyService.create(c, cookie);
        }
    }

    private static String dtoToCsvLine(EmployeeCsvDto d) {
        return String.join(",",
                d.getNumero(),
                d.getLastName(),
                d.getFirstName(),
                d.getGender(),
                d.getDateEmbauche(),
                d.getDateNaissance(),
                d.getCompany());
    }

    private static String generateAbbreviation(String companyName) {
        return Arrays.stream(companyName.split(" "))
                    .filter(word -> !word.isBlank())
                    .map(word -> word.substring(0, 1).toUpperCase())
                    .collect(Collectors.joining());
    }
}
