package com.application.tennisApplication.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {

    @GetMapping("/")
    public String index(){
        return "index";
    }

    @GetMapping("/WTARanking")
    public String WTARanking(){
        return "WTARanking";
    }

    @GetMapping("/ATPRanking")
    public String ATPRanking(){
        return "ATPRanking";
    }
}
