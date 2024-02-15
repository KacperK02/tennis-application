package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.FollowRepository;
import com.application.tennisApplication.service.FollowService;
import com.application.tennisApplication.service.PlayerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class PlayerController {

    @Autowired
    PlayerController playerController;
    @Autowired
    PlayerService playerService;

    @Autowired
    FollowRepository followRepository;

    @Autowired
    FollowService followService;

    @GetMapping("/WTARanking")
    public String showWTAPlayers(Model model, HttpSession session){
        List<Player> players = playerService.getAllWTAPlayers();

        User user = (User) session.getAttribute("user");

        boolean isFollowing = false;
        for (Player player : players){
            if (user != null) {
                isFollowing = followService.isPlayerFollowedByUser(player.getPlayerID(), user.getUserID());
            }
            player.setFollowing(isFollowing);
        }

        model.addAttribute("players", players);
        return "WTARanking";
    }

    @GetMapping("/ATPRanking")
    public String showATPPlayers(Model model, HttpSession session){
        List<Player> players = playerService.getAllATPPlayers();

        User user = (User) session.getAttribute("user");

        boolean isFollowing = false;
        for (Player player : players){
            if (user != null) {
                isFollowing = followService.isPlayerFollowedByUser(player.getPlayerID(), user.getUserID());
            }
            player.setFollowing(isFollowing);
        }

        model.addAttribute("players", players);
        return "ATPRanking";
    }

    @GetMapping("/playerInfo")
    public String playerInfo(@RequestParam("playerID") int playerId, Model model){
        Optional<Player> playerOptional = playerService.getPlayerById(playerId);
        if (playerOptional.isPresent()){
            Player player = playerOptional.get();
            model.addAttribute("player", player);
        }
        return "playerInfo";
    }
}
