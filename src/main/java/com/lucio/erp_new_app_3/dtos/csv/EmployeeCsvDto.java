package com.lucio.erp_new_app_3.dtos.csv;

import com.opencsv.bean.CsvBindByPosition;

import lombok.Data;

@Data
public class EmployeeCsvDto {
    @CsvBindByPosition(position = 0)
    private String numero;

    @CsvBindByPosition(position = 1)
    private String lastName;

    @CsvBindByPosition(position = 2)
    private String firstName;

    @CsvBindByPosition(position = 3)
    private String gender;

    @CsvBindByPosition(position = 4)
    private String dateEmbauche;

    @CsvBindByPosition(position = 5)
    private String dateNaissance;

    @CsvBindByPosition(position = 6)
    private String company;
}
