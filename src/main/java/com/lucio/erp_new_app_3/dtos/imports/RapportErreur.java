package com.lucio.erp_new_app_3.dtos.imports;

import lombok.Data;

@Data
public class RapportErreur {
    private String fichier;
    private String ligne;
    private String raison;
    private String valeur;
}
