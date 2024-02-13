package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.User;
import com.application.tennisApplication.service.FollowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AccountController {

    @Autowired
    private FollowService followService;

    @GetMapping("/account")
    public String account(HttpSession session, Model model){
        if (session.getAttribute("user") != null){
            User user = (User) session.getAttribute("user");
            List<Player> followedPlayers = followService.getFollowedPlayers(user.getUserID());
            model.addAttribute("followed_players", followedPlayers);
            return "account";
        }
        else{
            model.addAttribute("login_error", "Najpierw musisz się zalogować na swoje konto.");
            model.addAttribute("user_not_logged", true);
            model.addAttribute("user_logged", false);
            return "index";
        }
    }
}
