package com.application.tennisApplication.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @GetMapping("/account")
    public String account(HttpSession session, Model model){
        if (session.getAttribute("user") != null){
            return "account";
        }
        else{
            model.addAttribute("login_error", "Najpierw musisz się zalogować na swoje konto.");
            return "index";
        }
    }
}
