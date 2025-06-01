package com.lucio.erp_new_app_3.services.csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.lucio.erp_new_app_3.utils.DateValidator;

public class CsvEmployeImporter {

    public record ResultatImport(String message, List<String> erreurs, List<String> lignesErronees) {}

    public static ResultatImport traiterCsvEmployes(MultipartFile fichierEmploye) {
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

            }

        }
        catch (IOException e) {
            return new ResultatImport("Erreur de lecture du fichier.", List.of(e.getMessage()), lignesErronees);
        }

        String message = erreurs.isEmpty() ? "Import réussi !" : "Import terminé avec des erreurs.";
        return new ResultatImport(message, erreurs, lignesErronees);
    }

}
