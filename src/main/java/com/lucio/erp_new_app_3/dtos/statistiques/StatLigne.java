package com.lucio.erp_new_app_3.dtos.statistiques;

import java.util.Map;

import lombok.Data;

@Data
public class StatLigne {
    private String mois;
    private double total;
    private Map<String, Double> elements;
}
