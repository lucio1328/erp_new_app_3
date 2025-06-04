package com.lucio.erp_new_app_3.dtos.imports;

import com.opencsv.bean.CsvBindByName;

import lombok.Data;

@Data
public class SalaireData {
    @CsvBindByName(column = "Mois")
    private String mois;

    @CsvBindByName(column = "Ref Employe")
    private String refEmploye;

    @CsvBindByName(column = "Salaire Base")
    private Double salaireBase;

    @CsvBindByName(column = "Salaire")
    private String salaryStructure;

}