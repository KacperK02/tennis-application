package com.application.tennisApplication.controller;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.model.Match;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.Tournament;
import com.application.tennisApplication.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Controller
@CrossOrigin
public class PlayerController {
    @Autowired
    PlayerService playerService;

    @GetMapping("/getAllWTAPlayers")
    @ResponseBody
    public List<Player> getWTAPlayers(){
        List <Player> players = playerService.getAllWTAPlayers();
        players.sort(Comparator.comparingInt(Player::getRanking));
        return players;
    }

    @GetMapping("/getAllATPPlayers")
    @ResponseBody
    public List<Player> getATPPlayers(){
        List <Player> players = playerService.getAllATPPlayers();
        players.sort(Comparator.comparingInt(Player::getRanking));
        return players;
    }

    @GetMapping("/player/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable int id) {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            return ResponseEntity.ok(player);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/getPlayer/{teamid}")
    public ResponseEntity<Player> getPlayerByTeamId(@PathVariable int teamid) {
        Optional<Player> playerOptional = playerService.getPlayerByTeamId(teamid);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            return ResponseEntity.ok(player);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/{id}/getMatches")
    public ResponseEntity<List<Match>> getPlayerMatches(@PathVariable int id) throws JsonProcessingException {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            APIConnection apiConnection = new APIConnection();
            String response = apiConnection.getPlayerNearEvent(String.valueOf(player.getTeamid()));
            List<Match> matches = new ArrayList<>();
            Match lastMatch = playerService.getPlayerMatch(response, "previous");
            Match nextMatch = playerService.getPlayerMatch(response, "next");
            matches.add(lastMatch);
            matches.add(nextMatch);
            return ResponseEntity.ok(matches);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/{id}/lastTournaments")
    public ResponseEntity<List<Tournament>> getPlayerLastTournaments(@PathVariable int id) throws IOException {
        Optional<Player> playerOptional = playerService.getPlayerById(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            APIConnection apiConnection = new APIConnection();
            String response = apiConnection.getPlayerLastTournaments(String.valueOf(player.getTeamid()));
            List <Tournament> tournaments = playerService.getPlayerTournaments(response);
            return ResponseEntity.ok(tournaments);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/player/photoExists/{teamID}")
    public ResponseEntity<Boolean> checkPhotoExists(@PathVariable String teamID) {
        String photoPath = "frontend/src/assets/playerPhotos/" + teamID + ".png";
        File file = new File(photoPath);
        if (file.exists()) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.ok(false);
        }
    }

    @PostMapping("/player/fetchPhoto/{teamID}")
    public ResponseEntity<Void> fetchPlayerPhoto(@PathVariable String teamID) {
        APIConnection apiConnection = new APIConnection();
        apiConnection.getPlayerPhoto(teamID);
        return ResponseEntity.ok().build();
    }
}
