package com.lucio.erp_new_app_3.services.statistiques;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.lucio.erp_new_app_3.dtos.statistiques.StatLigne;

@Service
public class StatistiquesService {
    public List<StatLigne> calculerStatistiques(Integer annee) {
        List<StatLigne> statLignes = new ArrayList<>();

        StatLigne st1 = new StatLigne();
        st1.setMois("Janvier");
        st1.setTotal(100000);

        return statLignes;
    }
}
