package com.lucio.erp_new_app_3.utils;

import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

public class EnvoyeInformation {

    public static void setInfo(ModelAndView modelAndView, String titre, String page) {
        if (modelAndView != null) {
            modelAndView.addObject("title", titre);
            if (page == null || page == "") {
                modelAndView.addObject("page", "default/home");
                return;
            }
            modelAndView.addObject("page", page);
        }
        return;
    }

    public void afficherName(HttpSession session, ModelAndView modelAndView) {
        String loggedUser = (String) session.getAttribute("loggedUser");
        if (loggedUser == null) {
            loggedUser = "Utilisateur inconnu";
        }
        modelAndView.addObject("loggedUser", loggedUser);
    }
}
