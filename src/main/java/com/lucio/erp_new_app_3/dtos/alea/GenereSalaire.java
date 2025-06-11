package com.lucio.erp_new_app_3.dtos.alea;

import java.time.YearMonth;

import lombok.Data;

@Data
public class GenereSalaire {
    String employe;
    YearMonth moisDebut;
    YearMonth moisFin;
    String salaire;
}
