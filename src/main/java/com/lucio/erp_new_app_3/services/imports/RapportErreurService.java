package com.lucio.erp_new_app_3.services.imports;

import org.springframework.stereotype.Service;

import com.lucio.erp_new_app_3.dtos.imports.RapportErreur;

@Service
public class RapportErreurService {
    public RapportErreur createError(int ligne, String raison, String valeur) {
        RapportErreur rapportErreur = new RapportErreur();
        rapportErreur.setFichier("file 2");
        rapportErreur.setLigne(ligne + "");
        rapportErreur.setRaison(raison);
        rapportErreur.setValeur(valeur);
        return rapportErreur;
    }
}
