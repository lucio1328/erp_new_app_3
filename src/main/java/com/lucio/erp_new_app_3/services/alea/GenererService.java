package com.lucio.erp_new_app_3.services.alea;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lucio.erp_new_app_3.dtos.alea.GenereSalaire;
import com.lucio.erp_new_app_3.dtos.imports.SalaireData;
import com.lucio.erp_new_app_3.dtos.salary.assignment.StructureAssignement;
import com.lucio.erp_new_app_3.services.imports.SalaireImportService;
import com.lucio.erp_new_app_3.services.salary.SalaryAssignmentService;

@Service
public class GenererService {
    @Autowired
    private SalaryAssignmentService salaryAssignmentService;

    @Autowired
    private SalaireImportService salaireImportService;

    public void genererSalaire(String sessionCookie, GenereSalaire genereSalaire) throws Exception {
        StructureAssignement structureAssignement = salaryAssignmentService.getLatestAssignmentBeforeDate(genereSalaire.getEmploye(), sessionCookie, genereSalaire.getMoisDebut(), null);
        List<SalaireData> salaireDatas = construireSalaireData(structureAssignement, genereSalaire);
        salaireImportService.importSalaireData(sessionCookie, salaireDatas);
    }

    public List<SalaireData> construireSalaireData(StructureAssignement structureAssignement, GenereSalaire genereSalaire) {
        List<SalaireData> salaireDatas = new ArrayList<>();

        YearMonth current = genereSalaire.getMoisDebut();
        YearMonth end = genereSalaire.getMoisFin();

        boolean hasSalaire = genereSalaire.getSalaire() != null && !genereSalaire.getSalaire().isBlank();
        Double salaireFromRequest = null;
        try {
            salaireFromRequest = hasSalaire ? Double.valueOf(genereSalaire.getSalaire()) : null;
        }
        catch (NumberFormatException e) {
            salaireFromRequest = null;
        }

        while (!current.isAfter(end)) {
            SalaireData data = new SalaireData();
            data.setMois(current.toString());
            data.setRefEmploye(genereSalaire.getEmploye());

            if (salaireFromRequest != null && salaireFromRequest > 0) {
                data.setSalaireBase(salaireFromRequest);
            }
            else {
                data.setSalaireBase(structureAssignement.getBase());
            }

            data.setSalaryStructure(structureAssignement.getSalary_structure());

            salaireDatas.add(data);
            current = current.plusMonths(1);
        }

        return salaireDatas;
    }
}
