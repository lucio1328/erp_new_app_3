package com.lucio.erp_new_app_3.exceptions;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ErpApiException.class)
    public ModelAndView handleErpApiException(ErpApiException ex) {
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        modelAndView.addObject("view", "pages/error/erreur-generale");
        modelAndView.addObject("titre", "Erreur API ERP");

        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("status", ex.getStatusCode());
        modelAndView.addObject("message", ex.getMessage());

        return modelAndView;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGenericException(Exception ex) {
        ModelAndView modelAndView = new ModelAndView("layout/modele");

        modelAndView.addObject("view", "pages/error/erreur-generale");
        modelAndView.addObject("titre", "Erreur interne du serveur");

        modelAndView.addObject("timestamp", LocalDateTime.now());
        modelAndView.addObject("status", 500);
        modelAndView.addObject("message", ex.getMessage());

        return modelAndView;
    }
}
