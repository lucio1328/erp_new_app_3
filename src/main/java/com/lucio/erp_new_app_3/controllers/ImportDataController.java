package com.lucio.erp_new_app_3.controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.imports.EmployeData;
import com.lucio.erp_new_app_3.dtos.imports.GrilleSalaireData;
import com.lucio.erp_new_app_3.dtos.imports.ResultatImport;
import com.lucio.erp_new_app_3.dtos.imports.SalaireData;
import com.lucio.erp_new_app_3.services.imports.EmployeeImportService;
import com.lucio.erp_new_app_3.services.imports.GrilleImportService;
import com.lucio.erp_new_app_3.services.imports.SalaireImportService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@SuppressWarnings(value = "unused")
@Controller
@RequestMapping("/imports")
public class ImportDataController {
    @Autowired
    private EmployeeImportService importService;

    @Autowired
    private GrilleImportService grilleImportService;

    @Autowired
    private SalaireImportService salaireImportService;

    @GetMapping
    public ModelAndView form(HttpSession session){
        ModelAndView modelAndView=new ModelAndView("layout/modele");

        EnvoyeInformation.afficherName(session, modelAndView);
        modelAndView.addObject("page","pages/import/form");

        return modelAndView;
    }

    @PostMapping
    public ModelAndView imports(HttpSession session,@RequestParam("file1") MultipartFile file1,@RequestParam("file2") MultipartFile file2,@RequestParam("file3") MultipartFile file3){
        ModelAndView modelAndView=new ModelAndView("layout/modele");
        modelAndView.addObject("page","pages/import/form");
        ResultatImport resultatImport=new ResultatImport();
        try {
            importService.importEmployesFromCSV(resultatImport,file1);
            grilleImportService.importGrilleSalaireFromCSV(resultatImport, file2);
            salaireImportService.importSalairesFromCSV(resultatImport, file3);

            List<EmployeData> employeDatas=resultatImport.getEmployesValides();
            Map<String,String> refEmp=importService.createEmployees(session, employeDatas);

            List<GrilleSalaireData> grilleSalaireDatas=resultatImport.getGrilleSalaireDatas();
            grilleImportService.importGrilleSalaire(session, grilleSalaireDatas);

            List<SalaireData> salaireDatas=salaireImportService.transformeEmploye(resultatImport.getSalaireDatas(), refEmp);

            modelAndView.addObject("erreur1", resultatImport.getErreursEmploye());
            modelAndView.addObject("erreur2", resultatImport.getErreursGrille());
            modelAndView.addObject("erreur3", resultatImport.getErreursSalaire());

            EnvoyeInformation.afficherName(session, modelAndView);

            if(resultatImport.getErreursEmploye().isEmpty() && resultatImport.getErreursGrille().isEmpty() && resultatImport.getErreursSalaire().isEmpty()){ 
                modelAndView.addObject("successGlobal", "Importation réussi");
            }

        }
        catch (Exception e) {
            modelAndView.addObject("errorGlobal",e.getMessage());
        }
        return modelAndView;
    }
}

