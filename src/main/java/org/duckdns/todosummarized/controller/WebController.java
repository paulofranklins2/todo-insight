package org.duckdns.todosummarized.controller;

import lombok.RequiredArgsConstructor;
import org.duckdns.todosummarized.config.UiProperties;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Web controller for Thymeleaf UI pages.
 * Pages are shells that load data from API endpoints.
 */
@Controller
@RequiredArgsConstructor
public class WebController {

    private final UiProperties uiProperties;

    /**
     * Adds global model attributes available to all templates.
     */
    @ModelAttribute
    public void addGlobalAttributes(Model model) {
        model.addAttribute("appName", uiProperties.getAppName());
        model.addAttribute("appVersion", uiProperties.getVersion());
        model.addAttribute("apiBase", uiProperties.getApiBase());
        model.addAttribute("dateFormat", uiProperties.getDateFormat());
        model.addAttribute("dateTimeFormat", uiProperties.getDateTimeFormat());
    }

    /**
     * Login page - public access.
     */
    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    /**
     * Registration page - public access.
     */
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    /**
     * Dashboard/home page - requires authentication.
     */
    @GetMapping({"/", "/dashboard", "/todos"})
    public String dashboardPage() {
        return "dashboard";
    }
}

