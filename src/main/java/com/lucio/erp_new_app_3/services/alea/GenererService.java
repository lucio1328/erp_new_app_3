package com.lucio.erp_new_app_3.services.alea;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.lucio.erp_new_app_3.dtos.alea.GenereSalaire;
import com.lucio.erp_new_app_3.dtos.imports.SalaireData;
import com.lucio.erp_new_app_3.dtos.salary.assignment.StructureAssignement;
import com.lucio.erp_new_app_3.dtos.salary.details_salary.SalaryEarning;
import com.lucio.erp_new_app_3.dtos.salary.slip.SalarySlip;
import com.lucio.erp_new_app_3.exceptions.ErpApiException;
import com.lucio.erp_new_app_3.services.imports.SalaireImportService;
import com.lucio.erp_new_app_3.services.salary.SalaryAssignmentService;
import com.lucio.erp_new_app_3.services.salary.SalarySlipService;

@Service
public class GenererService {
    @Autowired
    private SalaryAssignmentService salaryAssignmentService;

    @Autowired
    private SalarySlipService salarySlipService;

    @Autowired
    private SalaireImportService salaireImportService;

    public List<SalarySlip> modifierSalarySlip(String sessioCookie) {
        List<SalarySlip> salarySlips = salarySlipService.getSalarySlips(sessioCookie);
        for (SalarySlip salarySlip : salarySlips) {
            salarySlip = salarySlipService.getSalarySlip(salarySlip.getName(), sessioCookie);
        }
        return salarySlips;
    }

    public Double moyenneSalaire(String sessionCookie) {
        List<SalarySlip> salarySlips = modifierSalarySlip(sessionCookie);

        System.out.println("Salary Slip : "+ salarySlips);

        Double moyenne = 0.0;
        Double somme = 0.0;
        for (SalarySlip salarySlip : salarySlips) {
            for (SalaryEarning salaryEarning : salarySlip.getEarnings()) {
                if (salaryEarning.getSalaryComponent().equals("Salaire Base")) {
                    somme += salaryEarning.getAmount();
                }
            }
        }
        moyenne = somme / salarySlips.size();
        return moyenne;
    }

    public void genererSalaire(String sessionCookie, GenereSalaire genereSalaire, Boolean ecraser, Boolean moyenne) throws Exception {
        Optional<StructureAssignement> structureAssignementOpt =
            salaryAssignmentService.getLatestAssignmentBeforeDate(
                genereSalaire.getEmploye(),
                sessionCookie,
                genereSalaire.getMoisDebut()
            );

        // StructureAssignement structureAssignement = structureAssignementOpt.orElseGet(() -> {
        //     if (genereSalaire.getSalaire() != null && !genereSalaire.getSalaire().isBlank()) {
        //         StructureAssignement fallback = new StructureAssignement();
        //         fallback.setBase(Double.valueOf(genereSalaire.getSalaire()));
        //         fallback.setSalary_structure(salaryStructureService.getAllStructure(sessionCookie).get(0).getName());
        //         return fallback;
        //     }
        //     else {
        //         throw new ErpApiException(
        //             "Aucune structure d'assignation trouvée et aucun salaire fourni.",
        //             HttpStatus.NOT_FOUND.value()
        //         );
        //     }
        // });

        StructureAssignement structureAssignement = structureAssignementOpt.orElseThrow(() -> {
                throw new ErpApiException(
                    "Aucune structure d'assignation trouvée et aucun salaire fourni.",
                    HttpStatus.NOT_FOUND.value()
                );
            });

        List<SalaireData> salaireDatas = construireSalaireData(structureAssignement, genereSalaire, sessionCookie, ecraser, moyenne);
        salaireImportService.importSalaireData(sessionCookie, salaireDatas);
    }

    public List<SalaireData> construireSalaireData(StructureAssignement structureAssignement, GenereSalaire genereSalaire, String sessionCookie, Boolean ecraser, Boolean moyenne) {
        List<SalaireData> salaireDatas = new ArrayList<>();

        LocalDate dateDebut = genereSalaire.getMoisDebut();
        LocalDate dateFin = genereSalaire.getMoisFin();

        boolean hasSalaire = genereSalaire.getSalaire() != null && !genereSalaire.getSalaire().isBlank();
        Double salaireFromRequest = null;
        try {
            salaireFromRequest = hasSalaire ? Double.valueOf(genereSalaire.getSalaire()) : null;
        }
        catch (NumberFormatException e) {
            salaireFromRequest = null;
        }

        while (!dateDebut.isAfter(dateFin)) {
            SalaireData data = new SalaireData();
            data.setMois(dateDebut.toString());
            data.setRefEmploye(genereSalaire.getEmploye());

            if (salaireFromRequest != null && salaireFromRequest > 0) {
                data.setSalaireBase(salaireFromRequest);
            }
            else {
                data.setSalaireBase(structureAssignement.getBase());
            }

            if (moyenne) {
                data.setSalaireBase(moyenneSalaire(sessionCookie));
            }

            data.setSalaryStructure(structureAssignement.getSalary_structure());
            SalarySlip salarySlip = salarySlipService.isSalarySlipExiste(sessionCookie, genereSalaire.getEmploye(), dateDebut);

            if (salarySlip == null) {
                salaireDatas.add(data);
            }
            else if (salarySlip != null && ecraser) {
                salarySlipService.cancelSalarySlip(salarySlip.getName(), sessionCookie);
                salarySlipService.deleteSalarySlip(salarySlip.getName(), sessionCookie);
                salaireDatas.add(data);
            }
            dateDebut = dateDebut.plusMonths(1);
        }

        return salaireDatas;
    }
}
