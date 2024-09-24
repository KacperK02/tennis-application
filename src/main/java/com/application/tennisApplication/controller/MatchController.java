package com.application.tennisApplication.controller;

import com.application.tennisApplication.API.APIConnection;
import com.application.tennisApplication.model.Match;
import com.application.tennisApplication.service.MatchService;
import com.application.tennisApplication.service.PlayerService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Controller
@CrossOrigin
public class MatchController {

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    @GetMapping("/getLiveMatches")
    public ResponseEntity<List<Match>> getLiveMatches() throws JsonProcessingException {
        APIConnection apiConnection = new APIConnection();
        String response = apiConnection.getLiveMatches();
        List<Match> liveMatches = new ArrayList<>();

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode node = jsonNode.path("events");

        for (int i = 0; i < node.size(); i++) {
            liveMatches.add(matchService.getMatchInfo(node.path(i)));
        }

        return ResponseEntity.ok(liveMatches);
    }

    @GetMapping("/getMatchStats/{id}")
    public ResponseEntity<List<List<String>>> getMatchStats(@PathVariable int id) throws JsonProcessingException {
        APIConnection apiConnection = new APIConnection();
        String response = apiConnection.getMatchStats(id);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response);
        JsonNode node = jsonNode.path("statistics").path(0);

        //aces, double faults, 1st service, 2nd service, 1st serve point won, 2nd service points won, fh winners, bh winners,
        // fh ue, bh ue, break points saved
        List<List<String>> matchStats = new ArrayList<>();
        List<String> firstPlayerStats = playerService.getPlayerMatchStats(node, "home");
        List<String> secondPlayerStats = playerService.getPlayerMatchStats(node, "away");

        matchStats.add(firstPlayerStats);
        matchStats.add(secondPlayerStats);

        return ResponseEntity.ok(matchStats);
    }
}
