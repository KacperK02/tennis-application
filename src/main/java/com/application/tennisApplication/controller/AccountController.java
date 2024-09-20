package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.User;
import com.application.tennisApplication.service.FollowService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@CrossOrigin
public class AccountController {
    @Autowired
    private FollowService followService;

    @GetMapping("/account")
    public ResponseEntity<List<Player>> account(HttpSession session){
        if (session.getAttribute("user") != null){
            User user = (User) session.getAttribute("user");
            List<Player> followedPlayers = followService.getFollowedPlayers(user.getUserID());
            return ResponseEntity.ok(followedPlayers);
        }
        else{
            return null;
        }
    }
}
