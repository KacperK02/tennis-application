package com.application.tennisApplication.service;

import com.application.tennisApplication.model.Match;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface MatchService {
    Match getMatchInfo(JsonNode node);
    int whoServes(JsonNode node, List<Integer> firstPlayerScore, List<Integer> secondPlayerScore, List<Integer> gamePoints);
    List<Integer> getPlayerScore(JsonNode node, String player);
    String getSurface(JsonNode node);
    String getRankOfTournament(JsonNode node);
    String translateRound(String englishRound);
    String getMatchStatus(JsonNode node);
}
