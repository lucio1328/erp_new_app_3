package com.lucio.erp_new_app_3.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.lucio.erp_new_app_3.dtos.auth.LoginForm;
import com.lucio.erp_new_app_3.response.LoginResult;
import com.lucio.erp_new_app_3.services.AuthService;
import com.lucio.erp_new_app_3.utils.EnvoyeInformation;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private AuthService authService;

    @GetMapping("/")
    public String index(Model model) {
        return "index";
    }

    @PostMapping("/login")
    public ModelAndView processLogin(@RequestParam("username") String username, @RequestParam("password") String password, Model model, HttpSession session) {
        LoginResult result = authService.loginToERPNext(new LoginForm(username, password));

        ModelAndView modelAndView = new ModelAndView("layout/modele");
        if (result.isSuccess()) {
            String sessionCookie = result.getSessionCookie();
            session.setAttribute("sid", sessionCookie);
            System.out.println(sessionCookie);

            String loggedUser = authService.getLoggedUsername(sessionCookie);
            session.setAttribute("loggedUser", loggedUser);
            EnvoyeInformation.setInfo(modelAndView, "Page d'accueil - Gestion Employe", "");

            return modelAndView;
        }
        else {
            model.addAttribute("error", result.getMessage());
            modelAndView.setViewName("index");
            return modelAndView;
        }
    }

    @GetMapping("/deconnexion")
    public String logout(HttpSession session) {
        session.removeAttribute("sid");
        session.removeAttribute("loggedUser");
        session.invalidate();

        return "redirect:/";
    }
}
