package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Match;
import com.application.tennisApplication.model.Player;
import com.application.tennisApplication.model.Tournament;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface PlayerService {
    List<Player> getAllPlayers();
    List<Player> getAllWTAPlayers();
    List<Player> getAllATPPlayers();
    Optional<Player> getPlayerById(int id);
    Optional<Player> getPlayerByTeamId(int teamid);
    void updateRanking() throws IOException, InterruptedException;
    List<Tournament> getPlayerTournaments(String response) throws JsonProcessingException;
    List<String> getPlayerInfo(Player player, String seed);
    Match getPlayerMatch(String response, String whichMatch) throws JsonProcessingException;
    HashMap<String, String> getPlayerMatchStats(JsonNode node, String player);
}
