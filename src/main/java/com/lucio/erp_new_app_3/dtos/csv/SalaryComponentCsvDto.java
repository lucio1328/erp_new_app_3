package com.lucio.erp_new_app_3.dtos.csv;

import com.opencsv.bean.CsvBindByPosition;
import lombok.Data;

@Data
public class SalaryComponentCsvDto {
    @CsvBindByPosition(position = 0)
    private String salaryStructure;

    @CsvBindByPosition(position = 1)
    private String name;

    @CsvBindByPosition(position = 2)
    private String abbr;

    @CsvBindByPosition(position = 3)
    private String type;

    @CsvBindByPosition(position = 4)
    private String valeur;

    @CsvBindByPosition(position = 5)
    private String company;
}
