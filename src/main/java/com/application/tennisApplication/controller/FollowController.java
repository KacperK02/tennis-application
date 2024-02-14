package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.Follow;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.FollowRepository;
import com.application.tennisApplication.repository.PlayerRepository;
import com.application.tennisApplication.service.PlayerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
public class FollowController {

    @Autowired
    private PlayerService playerService;

    @Autowired
    private FollowRepository followRepository;

    @PostMapping("/newFollow")
    public String newFollow(@RequestParam("playerID") int playerID, @ModelAttribute Follow follow, HttpSession session, Model model){
        Optional<Player> players = playerService.getPlayerById(playerID);
        if (players.isPresent() && session.getAttribute("user") != null){
            Player player = players.get();
            User user = (User) session.getAttribute("user");
            follow.setUser(user);
            follow.setPlayer(player);
            followRepository.save(follow);
        }
        else {
            model.addAttribute("follow_not_allowed", "Musisz być zalogowany, by śledzić gracza.");
        }
        return "redirect:/account";
    }

    @PostMapping("/deleteFollow")
    public String deleteFollow(@RequestParam("playerID") int playerID, HttpSession session){
        List<Follow> follows = followRepository.findAll();
        Optional<Player> players = playerService.getPlayerById(playerID);
        if (players.isPresent() && session.getAttribute("user") != null){
            Player player = players.get();
            User user = (User) session.getAttribute("user");
            for (Follow follow : follows) {
                if (follow.getUser().getUserID() == user.getUserID() && follow.getPlayer().getPlayerID() == player.getPlayerID()) {
                    followRepository.delete(follow);
                    break;
                }
            }
        }
        return "redirect:/account";
    }

}
