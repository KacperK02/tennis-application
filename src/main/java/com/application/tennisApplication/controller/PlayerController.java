package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class PlayerController {

    @Autowired
    PlayerController playerController;

    @Autowired
    PlayerService playerService;
    @GetMapping("/WTARanking")
    public String showWTAPlayers(Model model){
        List<Player> players = playerService.getAllWTAPlayers();
        model.addAttribute("players", players);
        return "WTARanking";
    }

    @GetMapping("/ATPRanking")
    public String showATPPlayers(Model model){
        List<Player> players = playerService.getAllATPPlayers();
        model.addAttribute("players", players);
        return "ATPRanking";
    }
}
