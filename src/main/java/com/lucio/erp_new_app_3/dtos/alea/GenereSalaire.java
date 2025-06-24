package com.lucio.erp_new_app_3.dtos.alea;

import java.time.LocalDate;

import lombok.Data;

@Data
public class GenereSalaire {
    String employe;
    LocalDate moisDebut;
    LocalDate moisFin;
    String salaire;
}
