package com.application.tennisApplication.controller;

import com.application.tennisApplication.model.Follow;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.FollowRepository;
import com.application.tennisApplication.service.FollowService;
import com.application.tennisApplication.service.PlayerService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@CrossOrigin
public class FollowController {
    @Autowired
    private PlayerService playerService;
    @Autowired
    private FollowRepository followRepository;
    @Autowired
    private FollowService followService;

    @GetMapping("/follow/isFollowing/{playerID}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable int playerID, HttpSession session) {
        if (session.getAttribute("user") != null) {
            User user = (User) session.getAttribute("user");
            boolean isFollowing = followService.isPlayerFollowedByUser(playerID, user.getUserID());
            return ResponseEntity.ok(isFollowing);
        }
        return ResponseEntity.ok(false);
    }

    @PostMapping("/follow/followPlayer")
    public ResponseEntity<Void> followPlayer(@RequestBody int playerID, HttpSession session) {
        Optional<Player> playerOptional = playerService.getPlayerById(playerID);
        if (playerOptional.isPresent() && session.getAttribute("user") != null) {
            Player player = playerOptional.get();
            User user = (User) session.getAttribute("user");
            Follow follow = new Follow();
            follow.setUser(user);
            follow.setPlayer(player);
            followRepository.save(follow);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    @DeleteMapping("/follow/deleteFollow/{playerID}")
    public ResponseEntity<Void> stopFollowingPlayer(@PathVariable int playerID, HttpSession session) {
        Optional<Player> players = playerService.getPlayerById(playerID);
        if (players.isPresent() && session.getAttribute("user") != null){
            Player player = players.get();
            User user = (User) session.getAttribute("user");
            List<Follow> follows = followRepository.findAll();
            for (Follow follow : follows) {
                if (follow.getUser().getUserID() == user.getUserID() && follow.getPlayer().getPlayerID() == player.getPlayerID()) {
                    followRepository.delete(follow);
                    break;
                }
            }
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
