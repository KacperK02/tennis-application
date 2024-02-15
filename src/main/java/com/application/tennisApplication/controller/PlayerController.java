package com.application.tennisApplication.controller;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.Tournament;
import com.application.tennisApplication.model.User;
import com.application.tennisApplication.repository.FollowRepository;
import com.application.tennisApplication.service.FollowService;
import com.application.tennisApplication.service.PlayerService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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
    public String playerInfo(@RequestParam("playerID") int playerId, Model model) throws IOException, InterruptedException {
        Optional<Player> playerOptional = playerService.getPlayerById(playerId);
        if (playerOptional.isPresent()){
            Player player = playerOptional.get();

            APIConnection apiConnection = new APIConnection();
            String response = apiConnection.getPlayerLastTournaments(String.valueOf(player.getTeamid()));

            List <Tournament> tournaments = getPlayerTournaments(response);

            model.addAttribute("tournaments", tournaments);
            model.addAttribute("player", player);
        }
        return "playerInfo";
    }

    private List<Tournament> getPlayerTournaments(String response) throws IOException {
        List <Tournament> tournaments = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode jsonNode2 = jsonNode.path("uniqueTournaments");

        for (JsonNode node : jsonNode2){
            String name = node.path("name").asText();
            boolean winner = node.path("winner").asBoolean();

            String round;
            if (winner){
                round = "WYGRANA";
            }
            else {
                round = node.path("round").asText();
                if (round == null || round.isEmpty()) round = "przegrana";
            }

            int points;
            String rank;
            if (node.path("uniqueTournament").path("tennisPoints").isMissingNode()){
                rank = "brak danych";
            } else {
                points = node.path("uniqueTournament").path("tennisPoints").asInt();
                if (points == 2000) rank = "Wielki Szlem";
                else rank = String.valueOf(points);
            }

            tournaments.add(new Tournament(name, round, rank));
        }
        Collections.reverse(tournaments);
        return tournaments;
    }
}
