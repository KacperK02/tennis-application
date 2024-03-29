package com.application.tennisApplication.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index1(Model model, HttpSession session){
        if (session.getAttribute("user") == null){
            model.addAttribute("user_logged", false);
        }
        else{
            model.addAttribute("user_logged", true);
        }
        return "index";
    }

    @GetMapping("index")
    public String index2(Model model, HttpSession session){
        if (session.getAttribute("user") == null){
            model.addAttribute("user_logged", false);
        }
        else{
            model.addAttribute("user_logged", true);
        }
        return "index";
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }

    @PostMapping("/logout")
    public String logout(Model model, HttpSession session){
        session.invalidate();
        model.addAttribute("user_logged", false);
        return "index";
    }
}
